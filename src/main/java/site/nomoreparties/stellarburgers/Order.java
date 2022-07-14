package site.nomoreparties.stellarburgers;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

public class Order {

    @SerializedName("ingredients")
    private String[] ingredientsId;

    public Order(List<Ingredient> ingredientsId) {
        if (ingredientsId != null) {
            this.ingredientsId = ingredientsId.stream().map(k -> k.id).toArray(String[]::new);
        }
    }

    public void setRandomValuesForIngredients() {
        if (ingredientsId != null && ingredientsId.length > 0) {
            for (int i = 0; i < ingredientsId.length; i++) {
                ingredientsId[i] = RandomStringUtils.randomAlphabetic(30);
            }
        }
    }

    public String[] getIngredients() {
        return ingredientsId;
    }
}
