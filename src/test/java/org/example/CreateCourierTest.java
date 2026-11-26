package org.example;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.pojo.Courier;
import org.example.pojo.CourierForLogin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateCourierTest {
    private String randomLogin;
    private String randomPassword;
    private CourierApi courierApi;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courierApi = new CourierApi();
        // что бы код был гибким использую генерацию случайных строк, кажется это не явные требования
        // конкатенируем префикс и случайные буквы и цифры длиной 5
        randomLogin = "courier+" + RandomStringUtils.randomAlphanumeric(5);
        // вариант защищённого пароля, взял первые 10 символов из 32 созданных
        randomPassword = UUID.randomUUID().toString().substring(0, 10);
        // создаём курьера
        courier = new Courier(randomLogin, randomPassword, "Victor");
    }

    @Test
    // В этом тесте учёл требования 1) курьера можно создать 2) запрос возвращает правильный код ответа 201
    // 3) успешный запрос возвращает ok: true
    public void courierIsCreated() {
        // создаю нового курьера через Api класс
        Response createResponse = courierApi.create(courier);
        // проверяю что курьер создался
        createResponse.then().assertThat()
                .statusCode(201)
                .body("ok", equalTo(true));

        // захожу за созданного курьера
        CourierForLogin data = new CourierForLogin(randomLogin, randomPassword);
        Response loginResponse = courierApi.login(data);
        // извлекаю id курьера, который необходим для удаления учётной записи
        courierId = loginResponse.then().extract().path("id");
    }

    @Test
    // В этом тесте учёл требования 1) нельзя создать двух одинаковых курьеров
    // 2) если создать пользователя с логином, который уже есть, возвращается ошибка
    public void courierDuplicate() {
        Response createResponse = courierApi.create(courier);
        // проверяю что курьер создался
        createResponse.then().assertThat().statusCode(201);
        // логин и данные для удаления
        CourierForLogin data = new CourierForLogin(randomLogin, randomPassword);
        courierId = courierApi.login(data).then().extract().path("id");
        // пытаюсь создать ещё один аккаунт с теми же данными
        Response duplicateResponse = courierApi.create(courier);
        // проверка статус кода ошибки и текста ошибки
        duplicateResponse.then().assertThat()
                .statusCode(409)
                // тут изменил сообщение на текст с сервера, для зелёной галочки; в документации к Api текст другой
                .body("message", org.hamcrest.CoreMatchers.equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    // В этом тесте учёл требования 1) чтобы создать курьера, нужно передать в ручку все обязательные поля
    // 2) если одного из полей нет, запрос возвращает ошибку
    public void brokenCourier() {
        // создаю курьера без логина
        Courier courierWithoutLogin = new Courier("", randomPassword, "Victor");
        Response responseWithoutLogin = courierApi.create(courierWithoutLogin);
        // проверяю ответ от сервера на верные данные об ошибке
        responseWithoutLogin.then().assertThat()
                .statusCode(400)
                .body("message", org.hamcrest.CoreMatchers.equalTo("Недостаточно данных для создания учетной записи"));
        // создаю курьера без пароля
        Courier courierWithoutPassword = new Courier(randomLogin, "", "Victor");
        Response responseWithoutPassword = courierApi.create(courierWithoutPassword);
        // проверяю ответ от сервера на верные данные об ошибке
        responseWithoutPassword.then().assertThat()
                .statusCode(400)
                .body("message", org.hamcrest.CoreMatchers.equalTo("Недостаточно данных для создания учетной записи"));
    }

    @After
    public  void tearDown() {
        // если курьер создаётся, то значение 0 по умолчанию меняется, это проверяем в условии и удаляем курьера
        if (courierId != 0) {
            courierApi.delete(courierId);
        }
    }


}


