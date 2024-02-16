package guru.qa.niffler.pageobject;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class WelcomePage {

    private final SelenideElement loginBtn = $("a[href*='redirect']");
    private final SelenideElement registerBtn = $("a[href*='/register']");

    @Step("Нажать кнопку Login")
    public LoginPage clickLoginButton() {
        loginBtn.click();
        return new LoginPage();
    }

    @Step("Перейти в регистрацию")
    public void clickRegBtn() {
        loginBtn.click();
    }
}