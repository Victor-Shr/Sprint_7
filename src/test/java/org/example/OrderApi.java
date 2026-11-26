package org.example;

import io.restassured.response.Response;
import org.example.pojo.Order;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;

// создал отдельный класс для закза, отделив по смыслу заказ самоката от работы с курьером
public class OrderApi {
    @Step("Создание заказа")
    public Response create(Order order) {
        return given()
                .header("Content-type", "application/json")
                // передаю POJO объект и тут происходит не явная сериализация
                .body(order)
                .when()
                .post("/api/v1/orders");
    }
    @Step("Получение списка заказов")
    public Response getOrders() {
        return given()
                .when()
                .get("/api/v1/orders");
    }

}
