package guru.qa.niffler.pageobject;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class WelcomePage {

    private final SelenideElement loginBtn = $("a[href*='redirect']");

    @Step("Нажать кнопку Login")
    public LoginPage clickLoginButton() {
        loginBtn.click();
        return new LoginPage();
    }
}
