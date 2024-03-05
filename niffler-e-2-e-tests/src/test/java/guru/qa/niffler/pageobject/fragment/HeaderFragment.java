package guru.qa.niffler.pageobject.fragment;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$x;

public class HeaderFragment {

    private final SelenideElement mainBtn = $x("//li[@data-tooltip-id='main']");
    private final SelenideElement friendsBtn = $x("//li[@data-tooltip-id='friends']");
    private final SelenideElement peopleBtn = $x("//li[@data-tooltip-id='people']");
    private final SelenideElement profileBtn = $x("//li[@data-tooltip-id='profile']");

    @Step("Перейти в Main Page")
    public void clickMainBtm() {
        mainBtn.click();
    }

    @Step("Перейти в Friends")
    public void clickFriendsBtn() {
        friendsBtn.click();
    }

    @Step("Перейти в All People")
    public void clickAllPeopleBtn() {
        peopleBtn.click();
    }

    @Step("Перейти в Profile")
    public HeaderFragment clickProfileBtn() {
        profileBtn.click();
        return this;
    }
}
