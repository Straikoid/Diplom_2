package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@Feature("Courier accounts management")
@Story("Change account data")
public class ChangeAccountDataTests {
    private final AccountAPI accountAPI = new AccountAPI();
    private Account firstAccount;
    private Account secondAccount;
    private String authTokenFirstAccount;
    private String authTokenSecondAccount;

    @BeforeEach
    public void setup() {
        firstAccount = Account.getRandom();
        ValidatableResponse response = accountAPI.registerNewAccount(firstAccount);
        authTokenFirstAccount = response.assertThat().statusCode(SC_OK).extract().path("accessToken").toString();
        firstAccount = Account.getRandom();
        secondAccount = Account.getRandom();
        response = accountAPI.registerNewAccount(secondAccount);
        authTokenSecondAccount = response.assertThat().statusCode(SC_OK).extract().path("accessToken").toString();
    }

    @Test
    @DisplayName("Edit account with correct auth token")
    public void changeAccountDataWithCorrectAuthTokenSuccess() {
        accountAPI.editAccount(firstAccount, authTokenFirstAccount).assertThat().
                statusCode(SC_OK).and().
                body("success", equalTo(true)).and().
                body("user.email", equalTo(firstAccount.getEmail().toLowerCase())).and().
                body("user.name", equalTo(firstAccount.getName()));
    }

    @Test
    @DisplayName("Edit account with incorrect auth token")
    public void changeAccountDataWithIncorrectAuthTokenUnauthorized() {
        accountAPI.editAccount(firstAccount, RandomStringUtils.randomAlphabetic(30)).assertThat().
                statusCode(SC_UNAUTHORIZED).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Edit account with empty auth token")
    public void changeAccountDataWithEmptyAuthTokenUnauthorized() {
        accountAPI.editAccount(firstAccount, "").assertThat().
                statusCode(SC_UNAUTHORIZED).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Edit account without auth token")
    public void changeAccountDataWithoutTokenUnauthorized() {
        accountAPI.editAccount(firstAccount, null).assertThat().
                statusCode(SC_UNAUTHORIZED).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Edit account with correct auth token and with already existed name")
    public void changeAccountDataWithCorrectAuthTokenAndWithAlreadyExistedNameSuccess() {
        firstAccount.setName(secondAccount.getName());
        accountAPI.editAccount(firstAccount, authTokenFirstAccount).assertThat().
                statusCode(SC_OK).and().
                body("success", equalTo(true)).and().
                body("user.email", equalTo(firstAccount.getEmail().toLowerCase())).and().
                body("user.name", equalTo(secondAccount.getName()));
    }

    @Test
    @DisplayName("Edit account with correct auth token and with already existed email")
    public void changeAccountDataWithCorrectAuthTokenAndWithAlreadyExistedEmailForbidden() {
        firstAccount.setEmail(secondAccount.getEmail());
        accountAPI.editAccount(firstAccount, authTokenFirstAccount).assertThat().
                statusCode(SC_FORBIDDEN).and().
                body("success", equalTo(false)).
                body("message", equalTo("User with such email already exists"));
    }

    @Test
    @DisplayName("Edit account with correct auth token and with already existed email")
    public void changeAccountDataWithCorrectAuthTokenAndWithAlreadyExistedPasswordSuccess() {
        firstAccount.setPassword(secondAccount.getPassword());
        accountAPI.editAccount(firstAccount, authTokenFirstAccount).assertThat().
                statusCode(SC_OK).and().
                body("success", equalTo(true)).and().
                body("user.email", equalTo(firstAccount.getEmail().toLowerCase())).and().
                body("user.name", equalTo(firstAccount.getName()));
    }

    @AfterEach
    public void tearDown() {
        accountAPI.deleteAccount(authTokenFirstAccount).assertThat().statusCode(SC_ACCEPTED);
        accountAPI.deleteAccount(authTokenSecondAccount).assertThat().statusCode(SC_ACCEPTED);
    }
}
