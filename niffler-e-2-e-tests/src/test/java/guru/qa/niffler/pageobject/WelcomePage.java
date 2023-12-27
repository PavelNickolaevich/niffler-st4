package guru.qa.niffler.pageobject;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class WelcomePage {

    private final SelenideElement logintBtn = $(".spendings-table tbody");

    @Step("Нажать кнопку Login")
    public LoginPage clickLoginButton() {
        logintBtn.click();
        return new LoginPage();
    }
}
