package guru.qa.niffler.pageobject.fragment;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.SpendCollectionCondition.spends;

public class SpendingTable extends BaseComponent<SpendingTable> {


    private final ElementsCollection spendingButtons = self.$$(".spendings__buttons button");
    private final SelenideElement historyOfSpendingsTable = $(".spendings-table tbody");
    private final SelenideElement deleteSelectedBtn = $(byText("Delete selected"));

    public SpendingTable() {
        super($(".main-content__section-history"));
    }

    @Step("Click by button {0}")
    public SpendingTable clickByButton(String buttonText) {
        spendingButtons.find(Condition.text(buttonText))
                .scrollIntoView(false)
                .click();
        return this;
    }

    @Step("Check that table contains data {0}")
    public SpendingTable checkTableContains(SpendJson... expectedSpends) {
        self.$$("tbody tr").should(spends(expectedSpends));
        return this;
    }

    public SpendingTable editSpending(int row, SpendJson editedSpending) {
        SelenideElement rowElement = self.$$("tbody tr").get(row);
        rowElement.$(".button-icon_type_edit").click();
        setSpendingAmount(rowElement, editedSpending.amount());
        setSpendingDescription(rowElement, editedSpending.description());
        setSpendingCategory(rowElement, editedSpending.category());
        submitEditSpending(rowElement);
        return this;
    }

    private void setSpendingAmount(SelenideElement row, Double amount) {
        row.$("input[name=amount]").setValue(String.valueOf(amount));
    }

    private void setSpendingDescription(SelenideElement row, String description) {
        row.$("input[name=description]").setValue(String.valueOf(description));
    }

    private void setSpendingDate(SelenideElement row, Date date) {
        SelenideElement input = row.$(".react-datepicker-wrapper #editable__input");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        input.clear();
        input.setValue(dateFormat.format(date));
        self.click();
        self.scrollIntoView(false);
    }

    private void setSpendingCategory(SelenideElement row, String category) {
        row.$("input[id^='react-select']").scrollIntoView(false).setValue(category);
        row.$$("div[id^='react-select']").find(exactText(category)).click();
    }

    private void submitEditSpending(SelenideElement row) {
        row.$(".button-icon_type_submit").click();
    }

    @Step("Проверить, что таблица \"History of Spendings\"")
    public void checkTableIsEmpty() {
        historyOfSpendingsTable
                .$$("tr")
                .shouldHave(size(0));
    }

    @Step("Выбрать spend по тексту : {text}")
    public SpendingTable selectSpendingByText(String text) {
        historyOfSpendingsTable
                .$$("tr")
                .find(text(text))
                .$$("td")
                .first()
                .scrollIntoView(true)
                .click();
        return this;
    }

    @Step("Выбрать spend с описанием : {description}")
    public SpendingTable selectSpendingByDescription(String description) {
        historyOfSpendingsTable
                .$$("tr")
                .find(text(description))
                .$("td")
                .scrollIntoView(true)
                .click();
        return this;
    }

    @Step("Выбрать spend с описанием : {description}")
    public SpendingTable selectSpendingByIndex(int index) {
        historyOfSpendingsTable
                .$$("tr")
                .get(index)
                .$("td")
                .scrollIntoView(true)
                .click();
        return this;
    }

    @Step("Нажать кнопку \"Delete selected\"")
    public SpendingTable clickDeleteSelectedButton() {
        deleteSelectedBtn.click();
        return this;
    }

}
