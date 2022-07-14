package site.nomoreparties.stellarburgers;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;

public class Account {
    private String name;
    private String password;
    private String email;

    public Account(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public static Account getRandom() {
        Faker faker = new Faker(Locale.ENGLISH);
        String name = faker.name().firstName();
        String password = RandomStringUtils.randomAlphabetic(10);
        String email = name + "@email.com";
        return new Account(name, password, email);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
