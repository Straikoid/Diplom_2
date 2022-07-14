package site.nomoreparties.stellarburgers;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class AccountAPI extends RestAssuredClient {
    private static final String REGISTER_PATH = "api/auth/register";
    private static final String LOGIN_PATH = "api/auth/login";
    private static final String EDIT_PATH = "api/auth/user";

    @Step("Register new account")
    public ValidatableResponse registerNewAccount(Account account) {
        return given()
                .spec(getBaseSpec())
                .and()
                .body(account)
                .when()
                .post(REGISTER_PATH).then();
    }

    @Step("Delete account")
    public ValidatableResponse deleteAccount(String bearerToken) {
        return given()
                .spec(getBaseSpec())
                .and()
                .header("Authorization", bearerToken)
                .when().delete(EDIT_PATH).then();
    }

    @Step("Login account")
    public ValidatableResponse loginAccount(AccountCredentials credentials) {
        return given()
                .spec(getBaseSpec())
                .and()
                .body(credentials)
                .when()
                .post(LOGIN_PATH).then();
    }

    @Step("Edit account")
    public ValidatableResponse editAccount(Account account, String bearerToken) {
        if (bearerToken == null) {
            return given()
                    .spec(getBaseSpec())
                    .and()
                    .body(account)
                    .when()
                    .patch(EDIT_PATH).then();
        } else {
            return given()
                    .spec(getBaseSpec())
                    .and()
                    .header("Authorization", bearerToken)
                    .body(account)
                    .when()
                    .patch(EDIT_PATH).then();
        }
    }
}
