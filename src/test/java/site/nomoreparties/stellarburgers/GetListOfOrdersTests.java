package site.nomoreparties.stellarburgers;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@Feature("Orders management")
@Story("Get orders list")
public class GetListOfOrdersTests {
    private static final AccountAPI accountAPI = new AccountAPI();
    private static String authToken;
    private static final IngredientsAPI ingredientsAPI = new IngredientsAPI();
    private static final OrdersAPI ordersAPI = new OrdersAPI();
    private static Order firstOriginalOrder;
    private static Order secondOriginalOrder;

    @BeforeAll
    public static void setup() {
        Account account = Account.getRandom();
        authToken = accountAPI.registerNewAccount(account).extract().path("accessToken").toString();
        ValidatableResponse response = ingredientsAPI.getAllIngredients();
        List<Ingredient> allIngredients = response.extract().body().jsonPath().getList("data", Ingredient.class);
        firstOriginalOrder = new Order(List.of(allIngredients.get(0), allIngredients.get(1)));
        ordersAPI.createNewOrder(firstOriginalOrder, authToken);
        secondOriginalOrder = new Order(List.of(allIngredients.get(2), allIngredients.get(3)));
        ordersAPI.createNewOrder(secondOriginalOrder, authToken);
    }

    @Test
    @DisplayName("Get orders list without auth token")
    public void getOrdersWithoutAuthorizationTokenUnauthorized() {
        ordersAPI.getOrders(null).assertThat().
                statusCode(SC_UNAUTHORIZED).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Get orders list wit empty auth token")
    public void getOrdersWithEmptyAuthorizationTokenUnauthorized() {
        ordersAPI.getOrders("").assertThat().
                statusCode(SC_UNAUTHORIZED).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Get orders list wit empty auth token")
    public void getOrdersWithIncorrectAuthorizationTokenUnauthorized() {
        ordersAPI.getOrders(RandomStringUtils.randomAlphabetic(30)).assertThat().
                statusCode(SC_UNAUTHORIZED).and().
                body("success", equalTo(false)).and().
                body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Get orders list wit empty auth token")
    public void getOrdersWithCorrectAuthorizationTokenSuccess() {
        List<Order> receivedOrders =  ordersAPI.getOrders(authToken).assertThat().
                statusCode(SC_OK).and().
                body("success", equalTo(true)).and().
                extract().body().jsonPath().getList("orders", Order.class);
        Assertions.assertEquals(
                firstOriginalOrder.getIngredients()[0],
                receivedOrders.get(0).getIngredients()[0],
                "Incorrect ingredient returned");
        Assertions.assertEquals(
                firstOriginalOrder.getIngredients()[1],
                receivedOrders.get(0).getIngredients()[1],
                "Incorrect ingredient returned");
        Assertions.assertEquals(
                secondOriginalOrder.getIngredients()[0],
                receivedOrders.get(1).getIngredients()[0],
                "Incorrect ingredient returned");
        Assertions.assertEquals(
                secondOriginalOrder.getIngredients()[1],
                receivedOrders.get(1).getIngredients()[1],
                "Incorrect ingredient returned");
    }

    @AfterAll
    public static void tearDown() {
        accountAPI.deleteAccount(authToken).assertThat().statusCode(SC_ACCEPTED);
    }
}
