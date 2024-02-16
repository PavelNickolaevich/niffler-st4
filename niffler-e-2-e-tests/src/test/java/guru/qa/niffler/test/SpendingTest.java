package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.CategoryEntity;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.SpendEntity;
import guru.qa.niffler.db.repository.spend.SpendRepository;
import guru.qa.niffler.jupiter.UserRepositoryExtension;
import guru.qa.niffler.jupiter.annotations.GenerateCategory;
import guru.qa.niffler.jupiter.annotations.GenerateSpend;
import guru.qa.niffler.jupiter.annotations.GenerateSpendDB;
import guru.qa.niffler.jupiter.annotations.GenerateSpendRest;
import guru.qa.niffler.jupiter.extension.SpendRepositoryExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.pageobject.WelcomePage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Calendar;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ExtendWith(SpendRepositoryExtension.class)
public class SpendingTest extends BaseWebTest {

    private static final String USERNAME = "duck";
    private static final String PASSWORD = "12345";

    private SpendRepository spendRepository;
    private SpendEntity spendEntity;

    static {
        Configuration.browserSize = "1980x1024";
    }

    @GenerateCategory(
            username = "123",
            category = "Угар6"
    )
    @GenerateSpend(
            username = "123",
            description = "QA.GURU Advanced 5",
            amount = 72500.00,
            category = "Угар6",
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

    @Test
    @GenerateSpendRest(
            username = "123",
            description = "Judgment Day123",
            amount = 100000.00,
            category = "Rest",
            currency = CurrencyValues.RUB
    )
    void checkRestCreateCategoryAndSpend() {

    }

    @Test
    @GenerateSpendDB(
            username = "123",
            description = "Judgment Day321",
            amount = 100000.00,
            category = "DataBase",
            currency = CurrencyValues.RUB
    )
    void checkDBCreateCategoryAndSpend() {

    }



    @Test
    void checkSpendDataBase() {

        CategoryEntity category = new CategoryEntity();
        category.setCategory("Блэкджэк1");
        category.setUsername("Бендер1");

        spendEntity = new SpendEntity();
        spendEntity.setUsername("Бэндер1");
        spendEntity.setSpendDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
        spendEntity.setCurrency(guru.qa.niffler.db.model.CurrencyValues.EUR);
        spendEntity.setAmount(555.0);
        spendEntity.setDescription("Азартные игры");
        spendEntity.setCategory(category);

        spendRepository.createSpend(spendEntity);
    }

  @BeforeEach
  void doLogin() {
    Selenide.open("http://127.0.0.1:3000/main");
    $("a[href*='redirect']").click();
    $("input[name='username']").setValue("duck");
    $("input[name='password']").setValue("12345");
    $("button[type='submit']").click();
  }

  @GenerateSpend(
      username = "duck",
      description = "QA.GURU Advanced 4",
      amount = 72500.00,
      category = "Обучение",
      currency = CurrencyValues.RUB
  )
  @DisabledByIssue("74")
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

//    Allure.step("Delete spending", () -> $(byText("Delete selected"))
//        .click());
//
//    Allure.step("Check that spending was deleted", () -> {
//      $(".spendings-table tbody")
//          .$$("tr")
//          .shouldHave(size(0));
//    });
  }
}
