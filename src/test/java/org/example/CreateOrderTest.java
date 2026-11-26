package org.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.pojo.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    List<String> colours;
    private OrderApi orderApi;
    // конструктор класса со списком цветов
    public CreateOrderTest(List<String> colours) {
        this.colours = colours;
    }

@Parameterized.Parameters(name = "Цвет самоката: {0}")
// использую параметризацию, все 4 варианта сочетания цветов
public static Object[][] getColourScooter() {
    return new Object[][] {
            { List.of("BLACK") },
            { List.of("GREY") },
            { List.of( "BLACK", "GREY") },
            { List.of() },
    };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        orderApi = new OrderApi();
    }

    @Test
    // проверяю что заказ создался и тело ответа содержит track
    public void createOrder() {
        Order order = new Order(colours);
        Response response = orderApi.create(order);
        response.then().assertThat()
                .statusCode(201)
                .body("track", notNullValue());
    }


}
