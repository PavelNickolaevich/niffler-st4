package guru.qa.niffler.test_hw;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.jupiter.annotations.DbUser;
import guru.qa.niffler.test.BaseWebTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class RegistrationsTest extends BaseWebTest {

    @BeforeEach
    void doLogin() {
        Selenide.open("http://127.0.0.1:3000/main");
        welcomePage
                .clickRegBtn();

    }

    @DbUser()
    @Test
    void inputExistingName(UserAuthEntity userAuth) {
        registerPage
                .setUserName(userAuth.getUsername())
                .setPassword("repeat")
                .submitPassword("repeat")
                .submit()
                .checkErrorMessageUsernameDisplay("Username `%s` already exists", userAuth.getUsername());

    }

    @DbUser()
    @Test
    void inputNotEqualPassword(UserAuthEntity userAuth) {
        registerPage
                .setUserName(userAuth.getUsername())
                .setPassword("repeat")
                .submitPassword("repeat1")
                .submit()
                .checkErrorMessage("Passwords should be equal");

    }


    @ParameterizedTest(name = "{displayName} {0}")
    @ValueSource(strings = {"12", "1234567890123"})
    void inputWrongLengthPassword(String length) {
        registerPage
                .setUserName("test")
                .setPassword(length)
                .submitPassword(length)
                .submit()
                .checkErrorMessage("Allowed password length should be from 3 to 12 characters");

    }
}
