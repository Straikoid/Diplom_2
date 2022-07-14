package site.nomoreparties.stellarburgers;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrdersAPI extends RestAssuredClient {
    private static final String PATH = "api/orders";

    @Step("Create new order")
    public ValidatableResponse createNewOrder(Order order, String bearerToken) {
        if (bearerToken == null) {
            return given()
                    .spec(getBaseSpec())
                    .and()
                    .body(order)
                    .when()
                    .post(PATH).then();
        } else {
            return given()
                    .spec(getBaseSpec())
                    .and()
                    .header("Authorization", bearerToken)
                    .body(order)
                    .when()
                    .post(PATH).then();
        }
    }

    @Step("Get orders list")
    public ValidatableResponse getOrders(String bearerToken) {
        if (bearerToken == null) {
            return given()
                    .spec(getBaseSpec())
                    .when()
                    .get(PATH).then();
        } else {
            return given()
                    .spec(getBaseSpec())
                    .and()
                    .header("Authorization", bearerToken)
                    .when()
                    .get(PATH).then();
        }
    }
}
