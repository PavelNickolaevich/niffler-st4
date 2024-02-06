package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotations.GenerateCategory;
import guru.qa.niffler.jupiter.annotations.GenerateSpend;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.pageobject.WelcomePage;
import org.junit.jupiter.api.Test;


public class SpendingTest extends BaseWebTest {

    private static final String USERNAME = "duck";
    private static final String PASSWORD = "12345";

    static {
        Configuration.browserSize = "1980x1024";
    }

    @GenerateCategory(
            username = "duck",
            category = "Угар"
    )
    @GenerateSpend(
            username = "duck",
            description = "QA.GURU Advanced 4",
            amount = 72500.00,
            category = "Угар",
            currency = CurrencyValues.RUB
    )
    @Test
    void spendingShouldBeDeletedByButtonDeleteSpending(SpendJson spend) {

        Selenide.open("http://127.0.0.1:3000/main", WelcomePage.class)
                .clickLoginButton()
                .login(USERNAME, PASSWORD)
                .selectSpendingByDescription(spend.description())
                .clickDeleteSelectedButton()
                .checkTableIsEmpty();
    }
}
