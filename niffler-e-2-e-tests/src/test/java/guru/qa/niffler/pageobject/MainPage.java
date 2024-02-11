package guru.qa.niffler.pageobject;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage extends BasePage<MainPage> {

    private final SelenideElement historyOfSpendingsTable = $(".spendings-table tbody");
    private final SelenideElement deleteSelectedBtn = $(byText("Delete selected"));
    private final SelenideElement chooseSpendingCategory = $x("//div[contains(text(),'Choose spending category')]");
    private final SelenideElement nameCategory = $x("//div[contains(@id,'listbox')]");
    private final SelenideElement setAmount = $x("//input[@name='amount']");
    private final SelenideElement calendar = $x("//div[@class='calendar-wrapper']");
    private final SelenideElement descriptionSpending = $x("//input[@name='description']");
    private final SelenideElement addNewSpendingBtn = $x("//button[contains(text(),'Add new spending')]");
    private final SelenideElement todayBtn = $x("//button[contains(text(),'Today')]");
    private final SelenideElement lastWeekBtn = $x("//button[contains(text(),'Last week')]");
    private final SelenideElement lastMonthBtn = $x("//button[contains(text(),'Last month')]");
    private final SelenideElement allTimeBtn = $x("//button[contains(text(),'All time')]");


    @Step("Установить категорию : {categoryName}")
    public MainPage setCategoryName(String categoryName) {
        chooseSpendingCategory.click();
        nameCategory.$x(String.format(".//*[contains(text(), '%s')]", categoryName)).click();
        return this;
    }


    @Step("Установить  \"Amount\" : {amount}")
    public MainPage setSetAmount(String amount) {
        setAmount.setValue(amount);
        return this;
    }

    @Step("Уставноить дату : {dateTime}")
    public MainPage filterLastMonthBtn(String dateTime) {
        calendar.setValue(dateTime);
        return this;
    }

    @Step("Установить описание: {description}")
    public MainPage filterAllTimeBtn(String description) {
        descriptionSpending.setValue(description);
        return this;
    }

    @Step("Установить описание: {description}")
    public MainPage addNewSpending() {
        addNewSpendingBtn.click();
        return this;
    }

    @Step("Выбрать spend с описанием : {description}")
    public MainPage selectSpendingByDescription(String description) {
        historyOfSpendingsTable
                .$$("tr")
                .find(text(description))
                .$("td")
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


    @Step("Нажать на фильтр \"Today\"")
    public void filterTodayBtn() {
        todayBtn.click();
    }

    @Step("Нажать на фильтр \"Last week\"")
    public void filterLastWeekBtn() {
        lastWeekBtn.click();
    }

    @Step("Нажать на фильтр \"Last momth\"")
    public void filterLastMonthBtn() {
        lastMonthBtn.click();
    }

    @Step("Нажать на фильтр \"Akk time\"")
    public void filterAllTimeBtn() {
        allTimeBtn.click();
    }

}
