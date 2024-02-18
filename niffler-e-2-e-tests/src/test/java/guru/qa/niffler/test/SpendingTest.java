package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.SpendEntity;
import guru.qa.niffler.db.repository.spend.SpendRepository;
import guru.qa.niffler.jupiter.annotations.DisabledByIssue;
import guru.qa.niffler.jupiter.annotations.GenerateCategory;
import guru.qa.niffler.jupiter.annotations.GenerateSpend;
import guru.qa.niffler.jupiter.annotations.GenerateSpendRest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.pageobject.WelcomePage;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class SpendingTest extends BaseWebTest {

    private static final String USERNAME = "loki";
    private static final String PASSWORD = "12345";

    private SpendRepository spendRepository;
    private SpendEntity spendEntity;

    static {
        Configuration.browserSize = "1980x1024";
    }

    @GenerateCategory(
            username = "loki",
            category = "Угар7"
    )
    @GenerateSpendRest(
            username = "loki",
            description = "QA.GURU",
            amount = 72500.00,
            category = "Угар7",
            currency = CurrencyValues.RUB
    )
    @Test
    void spendingShouldBeDeletedByButtonDeleteSpendingV1(SpendJson spend) {

        Selenide.open("http://127.0.0.1:3000/main", WelcomePage.class)
                .clickLoginButton()
                .login(USERNAME, PASSWORD)
                .selectSpendingByDescription(spend.description())
                .clickDeleteSelectedButton()
                .checkTableIsEmpty();
    }

    @BeforeEach
    void doLogin() {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue("duck");
        $("input[name='password']").setValue("12345");
        $("button[type='submit']").click();
    }

    @GenerateSpendRest(
            username = "duck",
            description = "QA.GURU Advanced 4",
            amount = 72500.00,
            category = "Обучение",
            currency = CurrencyValues.RUB
    )

 //   @DisabledByIssue("74")
    @Test
    void spendingShouldBeDeletedByButtonDeleteSpending(SpendJson spend) {
        $(".spendings-table tbody")
                .$$("tr")
                .find(text(spend.description()))
                .$$("td")
                .first()
                .click();


        new MainPage()
                .getSpendingTable()
                .checkTableContains(spend);

//        Allure.step("Delete spending", () -> $(byText("Delete selected"))
//                .click());

//        Allure.step("Check that spending was deleted", () -> {
//            $(".spendings-table tbody")
//                    .$$("tr")
//                    .shouldHave(size(0));
//        });
    }
}

