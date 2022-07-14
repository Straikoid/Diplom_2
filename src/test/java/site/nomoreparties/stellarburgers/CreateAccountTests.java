package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@Feature("Courier accounts management")
@Story("Create new account")
public class CreateAccountTests {

    private final AccountAPI accountAPI = new AccountAPI();

    private Account account;
    private String authTokenFirstAccount;
    private String authTokenSecondAccount;

    @BeforeEach
    public void setup() {
        account = Account.getRandom();
    }

    @Test
    @DisplayName("Register account with unique name/password/email")
    public void registerNewCourierAccountWithUniqueFieldsSuccess() {
        ValidatableResponse response = accountAPI.registerNewAccount(account);
        authTokenFirstAccount = response.assertThat().
                statusCode(SC_OK).and().
                body("success", equalTo(true)).
                extract().path("accessToken").toString();
    }

    @Test
    @DisplayName("Register account with already existed username")
    public void registerNewCourierAccountWithAlreadyExistedNameSuccess() {
        authTokenFirstAccount = accountAPI.registerNewAccount(account).extract().path("accessToken").toString();
        Account secondAccount = Account.getRandom();
        secondAccount.setName(account.getName());
        ValidatableResponse response = accountAPI.registerNewAccount(secondAccount);
        authTokenSecondAccount = response.assertThat().
                statusCode(SC_OK).and().
                body("success", equalTo(true)).
                extract().path("accessToken").toString();
    }

    @Test
    @DisplayName("Register account with already existed password")
    public void registerNewCourierAccountWithAlreadyExistedPasswordSuccess() {
        authTokenFirstAccount = accountAPI.registerNewAccount(account).extract().path("accessToken").toString();
        Account secondAccount = Account.getRandom();
        secondAccount.setPassword(account.getPassword());
        ValidatableResponse response = accountAPI.registerNewAccount(secondAccount);
        authTokenSecondAccount = response.assertThat().
                statusCode(SC_OK).and().
                body("success", equalTo(true)).
                extract().path("accessToken").toString();
    }

    @Test
    @DisplayName("Register account with already existed email")
    public void registerNewCourierAccountWithAlreadyExistedEmailForbidden() {
        authTokenFirstAccount = accountAPI.registerNewAccount(account).extract().path("accessToken").toString();
        Account secondAccount = Account.getRandom();
        secondAccount.setEmail(account.getEmail());
        accountAPI.registerNewAccount(secondAccount).assertThat().
                statusCode(SC_FORBIDDEN).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Register account without email")
    public void registerNewCourierAccountWithoutEmailForbidden() {
        account.setEmail(null);
        accountAPI.registerNewAccount(account).assertThat().
                statusCode(SC_FORBIDDEN).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Register account without email")
    public void registerNewCourierAccountWithoutNameForbidden() {
        account.setName(null);
        accountAPI.registerNewAccount(account).assertThat().
                statusCode(SC_FORBIDDEN).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Register account without email")
    public void registerNewCourierAccountWithoutPasswordForbidden() {
        account.setPassword(null);
        accountAPI.registerNewAccount(account).assertThat().
                statusCode(SC_FORBIDDEN).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("Email, password and name are required fields"));
    }

    @AfterEach
    public void tearDown() {
        if (authTokenFirstAccount != null) {
            accountAPI.deleteAccount(authTokenFirstAccount).assertThat().statusCode(SC_ACCEPTED);
        }
        if (authTokenSecondAccount != null) {
            accountAPI.deleteAccount(authTokenSecondAccount).assertThat().statusCode(SC_ACCEPTED);
        }
    }
}
