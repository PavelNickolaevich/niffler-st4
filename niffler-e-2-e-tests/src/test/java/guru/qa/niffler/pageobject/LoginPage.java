package guru.qa.niffler.pageobject;

import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends ElementsContainer {

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement signInBtn = $("button[type='submit']");

    @Step("Авторизация пользователя: {username} c паролем: {password}")
    public MainPage login(String username, String password) {

        usernameInput.setValue(username);
        passwordInput.setValue(password);
        signInBtn.click();

        return new MainPage();
    }

}
