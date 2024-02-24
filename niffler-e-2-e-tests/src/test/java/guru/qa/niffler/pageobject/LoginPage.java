package guru.qa.niffler.pageobject;

import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.db.model.CurrencyValues;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class LoginPage extends ElementsContainer {

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement signInBtn = $("button[type='submit']");
    private final SelenideElement errorMessage = $x("//p[@class='form__error']");


    @Step("Авторизация пользователя: {username} c паролем: {password}")
    public MainPage login(String username, String password) {

        usernameInput.setValue(username);
        passwordInput.setValue(password);
        signInBtn.click();

        return new MainPage();
    }

    @Step("Авторизация пользователя: {username} c паролем: {password}")
    public LoginPage loginWithWrongPassword(String username, String password) {

        usernameInput.setValue(username);
        passwordInput.setValue(password);
        signInBtn.click();

        return this;
    }

    public void checkErrorMessageDisplay(String message) {
        $(byText(message)).shouldBe(visible);

    }

}
