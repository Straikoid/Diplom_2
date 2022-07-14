package site.nomoreparties.stellarburgers;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class IngredientsAPI extends RestAssuredClient {
    private static final String GET_PATH = "api/ingredients";

    @Step("Get ingredients list")
    public ValidatableResponse getAllIngredients() {
        return given()
                .spec(getBaseSpec())
                .and()
                .when()
                .get(GET_PATH).then();
    }
}
