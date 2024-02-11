package guru.qa.niffler.pageobject;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.page.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$x;

public class ProfilePage extends BasePage<ProfilePage> {

    private final SelenideElement avatarBtn = $x("//button[@class='profile__avatar-edit']");
    private final SelenideElement uploadAvatarBtn = $x("//input[@class='edit-avatar__input']");
    private final SelenideElement mainName = $x("//input[@name='firstname']");
    private final SelenideElement surName = $x("//input[@name='surname']");
    private final SelenideElement submitBtn = $x("//button[@type='submit']");
    private final SelenideElement category = $x("//input[@name='category']");
    private final SelenideElement createCategoryBtn = $x("//button[contains(text(),'Create')]");
    private final SelenideElement currencyListBox = $x("//div[contains(@id,'listbox')]");
    private final SelenideElement currency = $x("//div[@data-value]");


    @Step("Установить пользователя: {name}")
    public ProfilePage setName(String name) {
        mainName.setValue(name);
        return this;
    }

    @Step("Установить фамилию: {surname}")
    public ProfilePage setSurname(String surname) {
        surName.setValue(surname);
        return this;
    }

    @Step("Добавить новую категорию: {categoryName}")
    public ProfilePage addNewCategory(String categoryName) {
        category.setValue(categoryName);
        createCategoryBtn.click();
        return this;
    }

    @Step("Сменить валюту на : {currencyValue}")
    public ProfilePage selectCurrency(CurrencyValues currencyValue) {
        currency.click();
        currencyListBox.$x(String.format(".//*[contains(text(), '%s')]", currencyValue.name())).click();
        return this;
    }


    public ProfilePage submitProfile() throws InterruptedException {
        submitBtn.click();
        return this;
    }


}
