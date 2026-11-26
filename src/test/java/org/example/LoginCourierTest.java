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

import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginCourierTest {
    private String randomLogin;
    private String randomPassword;
    private CourierApi courierApi;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courierApi = new CourierApi();
        randomLogin = "courier+" + RandomStringUtils.randomAlphanumeric(5);
        randomPassword = UUID.randomUUID().toString().substring(0, 10);
        courier = new Courier(randomLogin, randomPassword, "Victor");
        // создаю учётную запись, проверяю результат
        Response createResponse = courierApi.create(courier);
        createResponse.then().assertThat()
                .statusCode(201);
        // авторизация
        CourierForLogin data = new CourierForLogin(randomLogin, randomPassword);
        Response loginResponse = courierApi.login(data);
        // проверяю ответ от сервера
        loginResponse.then().assertThat()
                .statusCode(200);
        courierId = loginResponse.then().extract().path("id");
    }

    @Test
    // позитивный кейс, проверяем 1) курьер может авторизоваться 2) успешный запрос возвращает id
    public void courierLoginOk() {
        // авторизация
        CourierForLogin data = new CourierForLogin(randomLogin, randomPassword);
        Response loginResponse = courierApi.login(data);
        // проверяю ответ от сервера
        loginResponse.then().assertThat()
                .statusCode(200)
                .body("id", notNullValue());
    }
    @Test
    // негативный сценарий, проверяем 1) система вернёт ошибку, если неправильно указать логин
    // 2) если авторизоваться под несуществующим пользователем, запрос возвращает ошибку
    public void invalidLogin() {
        // пытаюсь зайти под ошибочным логином
        CourierForLogin data = new CourierForLogin(randomLogin + "error", randomPassword);
        Response loginResponse = courierApi.login(data);
        // проверяю ответ от сервера на соответствие требованиям
        loginResponse.then().assertThat()
                .statusCode(404)
                .body("message", org.hamcrest.CoreMatchers.equalTo("Учетная запись не найдена"));
    }

    @Test
    // негативный сценарий, проверяем 1) для авторизации нужно передать все обязательные поля
    // 2) если какого-то поля нет, запрос возвращает ошибку
    public void emptyLogin() {
        // пытаюсь зайти с пустым логином
        CourierForLogin data = new CourierForLogin("", randomPassword);
        Response loginResponse = courierApi.login(data);
        // проверяю ответ от сервера на соответствие требованиям
        loginResponse.then().assertThat()
                .statusCode(400)
                .body("message", org.hamcrest.CoreMatchers.equalTo("Недостаточно данных для входа"));
    }

    @Test
    // негативный сценарий, проверяем 1) система вернёт ошибку, если неправильно указать пароль
    public void invalidPassword() {
        // пытаюсь зайти с ошибочным паролем
        CourierForLogin data = new CourierForLogin(randomLogin, randomPassword + "error");
        Response loginResponse = courierApi.login(data);
        // проверяю ответ от сервера на соответствие требованиям
        loginResponse.then().assertThat()
                .statusCode(404)
                .body("message", org.hamcrest.CoreMatchers.equalTo("Учетная запись не найдена"));
    }

    @Test
    // негативный сценарий, проверяем 1) система вернёт ошибку, если пароль будет пустым
    public void emptyPassword() {
        // пытаюсь зайти с пустым паролем
        CourierForLogin data = new CourierForLogin(randomLogin, "");
        Response loginResponse = courierApi.login(data);
        // проверяю ответ от сервера на соответствие требованиям
        loginResponse.then().assertThat()
                .statusCode(400)
                .body("message", org.hamcrest.CoreMatchers.equalTo("Недостаточно данных для входа"));
    }

    @After
    public  void tearDown() {
        // если курьер создаётся, то значение 0 по умолчанию меняется, это проверяем в условии и удаляем курьера
        if (courierId != 0) {
            courierApi.delete(courierId);
        }
    }
}
