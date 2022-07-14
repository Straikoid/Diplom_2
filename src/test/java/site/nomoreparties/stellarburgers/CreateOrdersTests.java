package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@Feature("Orders management")
@Story("Create new order")
public class CreateOrdersTests {
    private static List<Ingredient> allIngredients = new ArrayList<>();
    private static final IngredientsAPI ingredientsAPI = new IngredientsAPI();
    private static final AccountAPI accountAPI = new AccountAPI();
    private static String authToken;
    private final OrdersAPI ordersAPI = new OrdersAPI();

    @BeforeAll
    public static void setup() {
        Account account = Account.getRandom();
        authToken = accountAPI.registerNewAccount(account).extract().path("accessToken").toString();
        ValidatableResponse response = ingredientsAPI.getAllIngredients();
        allIngredients = response.extract().body().jsonPath().getList("data", Ingredient.class);
    }

    @Test
    @DisplayName("Create new order with ingredients and without auth token")
    public void createOrderWithIngredientsAndWithoutAuthorizationTokenUnauthorized() {
        Order order = new Order(List.of(allIngredients.get(0), allIngredients.get(1)));
        ordersAPI.createNewOrder(order, null).assertThat().
                statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Create new order with ingredients and with empty auth token")
    public void createOrderWithIngredientsAndWithEmptyAuthorizationTokenUnauthorized() {
        Order order = new Order(List.of(allIngredients.get(0), allIngredients.get(1)));
        ordersAPI.createNewOrder(order, "").assertThat().
                statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Create new order with ingredients and with incorrect auth token")
    public void createOrderWithIngredientsAndWithIncorrectAuthorizationTokenUnauthorized() {
        Order order = new Order(List.of(allIngredients.get(0), allIngredients.get(1)));
        ordersAPI.createNewOrder(order, RandomStringUtils.randomAlphabetic(30)).assertThat().
                statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Create new order with ingredients and with correct auth token")
    public void createOrderWithIngredientsAndWithCorrectAuthorizationTokenSuccess() {
        Order order = new Order(List.of(allIngredients.get(0), allIngredients.get(1)));
        ordersAPI.createNewOrder(order, authToken).assertThat().
                statusCode(SC_OK).and().
                body("success", equalTo(true));
    }

    @Test
    @DisplayName("Create new order without ingredients list and with correct auth token")
    public void createOrderWithoutIngredientsAndWithCorrectAuthorizationTokenBadRequest() {
        Order order = new Order(null);
        ordersAPI.createNewOrder(order, authToken).assertThat().
                statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Create new order with empty ingredients list and with correct auth token")
    public void createOrderWithEmptyIngredientsAndWithCorrectAuthorizationTokenBadRequest() {
        Order order = new Order(List.of());
        ordersAPI.createNewOrder(order, authToken).assertThat().
                statusCode(SC_BAD_REQUEST).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create new order with wrong ingredients and with correct auth token")
    public void createOrderWithIncorrectIngredientsAndWithCorrectAuthorizationTokenBadRequest() {
        Order order = new Order(List.of(allIngredients.get(0), allIngredients.get(1)));
        order.setRandomValuesForIngredients();
        ordersAPI.createNewOrder(order, authToken).assertThat().
                statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @AfterAll
    public static void tearDown() {
        accountAPI.deleteAccount(authToken).assertThat().statusCode(SC_ACCEPTED);
    }
}
