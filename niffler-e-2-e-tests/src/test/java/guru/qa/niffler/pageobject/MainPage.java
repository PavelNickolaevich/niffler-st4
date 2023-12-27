package guru.qa.niffler.pageobject;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {

    private final SelenideElement historyOfSpendingsTable = $(".spendings-table tbody");
    private final SelenideElement deleteSelectedBtn = $(byText("Delete selected"));

    @Step("Переход на страницу Main page")
    public MainPage mainPage() {
        return this;
    }

    @Step("Выбрать spend с описанием : {description}")
    public MainPage selectSpendingByDescription(String description) {
        historyOfSpendingsTable
                .$$("tr")
                .find(text(description))
                .$$("td")
                .first()
                .scrollIntoView(true)
                .click();
        return this;
    }

    @Step("Нажать кнопку \"Delete selected\"")
    public MainPage clickDeleteSelectedButton() {
        deleteSelectedBtn.click();
        return this;
    }

    @Step("Проверить, что таблица \"History of Spendings\"")
    public void checkTableIsEmpty() {
        historyOfSpendingsTable
                .$$("tr")
                .shouldHave(size(0));
    }

}
