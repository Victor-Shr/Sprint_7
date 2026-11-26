package org.example;

import io.restassured.response.Response;
import org.example.pojo.Courier;
import org.example.pojo.CourierForLogin;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;

// создал отдельный класс для ручек, что бы не повторять код несколько раз в тестах
public class CourierApi {
    // аннотация Step кажется нужна именно тут, что бы не повторять её в тестах
    @Step("Создание курьера в БД Самокат")
    public Response create(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                // передаю POJO объект и тут происходит не явная сериализация
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }
    // метод для авторизации курьера
    @Step("Курьер логинится в системе")
    public Response login(CourierForLogin courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }
    // метод для удаления курьера, тело тут передавать не нужно, только id курьера в URL
    @Step("Удаление курьера по ID")
    public void delete(int idCourier) {
            given()
                .when()
                .delete("/api/v1/courier/" + idCourier);
    }
}
