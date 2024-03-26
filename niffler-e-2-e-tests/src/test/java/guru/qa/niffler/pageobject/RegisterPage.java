package guru.qa.niffler.pageobject;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.xpath;

public class RegisterPage extends BasePage<RegisterPage> {

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement submitPasswordInput = $("#passwordSubmit");
    private final SelenideElement signUpBtn = $(xpath("//button[@type='submit']"));
    private final SelenideElement signUInBtn = $(byText("Sign in!"));



    @Step("Установить пользователя: {userName}")
    public RegisterPage setUserName(String userName) {
        usernameInput.setValue(userName);
        return this;
    }

    @Step("Установить пароль: {password}")
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Подтвердить пароль: {password}")
    public RegisterPage submitPassword(String password) {
        submitPasswordInput.setValue(password);
        return this;
    }

    @Step("Зарегистрировать нового пользователя")
    public RegisterPage submit() {
        signUpBtn.click();
        return this;
    }

    @Step("Войти")
    public RegisterPage signin() {
        signUInBtn.click();
        return this;
    }

    public void checkErrorMessageUsernameDisplay(String message, String username) {
        $(byText(String.format(message,username))).shouldBe(visible);
    }

    public void checkErrorMessage(String message) {
        $(byText(message)).shouldBe(visible);
    }

    @Override
    public RegisterPage waitForPageLoaded() {
        return null;
    }
}
