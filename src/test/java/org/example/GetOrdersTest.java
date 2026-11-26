package org.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;

public class GetOrdersTest {
    private OrderApi orderApi;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        orderApi = new OrderApi();
    }

    @Test
    //проверяю что ответ приходит и есть тело ответа
    public void ordersList() {
        Response response = orderApi.getOrders();
        response.then().assertThat()
                .statusCode(200)
                .body("orders", notNullValue());
    }

}
