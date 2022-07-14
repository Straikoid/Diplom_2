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
@Story("Login under created account")
public class LoginAccountTests {
    private final AccountAPI accountAPI = new AccountAPI();
    private AccountCredentials credentials;
    private String authToken;

    @BeforeEach
    public void setup() {
        Account account = Account.getRandom();
        credentials = new AccountCredentials(account.getEmail(), account.getPassword());
        ValidatableResponse response = accountAPI.registerNewAccount(account);
        authToken = response.assertThat().statusCode(SC_OK).extract().path("accessToken").toString();
    }

    @Test
    @DisplayName("Login with correct credentials")
    public void loginWithCorrectCredentialsSuccess() {
        accountAPI.loginAccount(credentials).assertThat().
                statusCode(SC_OK).and().
                body("success", equalTo(true));
    }

    @Test
    @DisplayName("Login with incorrect email in credentials")
    public void loginWithIncorrectEmailInCredentialsUnauthorized() {
        credentials.setEmail("changed" + credentials.getEmail());
        accountAPI.loginAccount(credentials).assertThat().
                statusCode(SC_UNAUTHORIZED).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Login without email in credentials")
    public void loginWithoutEmailInCredentialsUnauthorized() {
        credentials.setEmail(null);
        accountAPI.loginAccount(credentials).assertThat().
                statusCode(SC_UNAUTHORIZED).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Login with empty email in credentials")
    public void loginWithEmptyEmailInCredentialsUnauthorized() {
        credentials.setEmail("");
        accountAPI.loginAccount(credentials).assertThat().
                statusCode(SC_UNAUTHORIZED).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Login with incorrect password in credentials")
    public void loginWithIncorrectPasswordInCredentialsUnauthorized() {
        credentials.setPassword(RandomStringUtils.randomAlphabetic(10));
        accountAPI.loginAccount(credentials).assertThat().
                statusCode(SC_UNAUTHORIZED).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Login without password in credentials")
    public void loginWithoutPasswordInCredentialsUnauthorized() {
        credentials.setPassword(null);
        accountAPI.loginAccount(credentials).assertThat().
                statusCode(SC_UNAUTHORIZED).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Login with empty password in credentials")
    public void loginWithEmptyPasswordInCredentialsUnauthorized() {
        credentials.setPassword("");
        accountAPI.loginAccount(credentials).assertThat().
                statusCode(SC_UNAUTHORIZED).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("email or password are incorrect"));
    }

    @AfterEach
    public void tearDown() {
        accountAPI.deleteAccount(authToken).assertThat().statusCode(SC_ACCEPTED);
    }
}
