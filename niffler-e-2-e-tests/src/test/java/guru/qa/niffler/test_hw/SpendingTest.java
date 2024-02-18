package guru.qa.niffler.test_hw;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.CategoryEntity;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.SpendEntity;
import guru.qa.niffler.db.repository.spend.SpendRepository;
import guru.qa.niffler.jupiter.annotations.GenerateCategory;
import guru.qa.niffler.jupiter.annotations.GenerateSpendDB;
import guru.qa.niffler.jupiter.annotations.GenerateSpendRest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.pageobject.WelcomePage;
import guru.qa.niffler.test.BaseWebTest;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

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

}

