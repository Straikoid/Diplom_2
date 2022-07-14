package site.nomoreparties.stellarburgers;

import com.google.gson.annotations.SerializedName;

public class Ingredient {
    @SerializedName("_id")
    String id;
    String name;
    String type;
    float price;

    public Ingredient(String id, String name, String type, float price) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
    }
}
