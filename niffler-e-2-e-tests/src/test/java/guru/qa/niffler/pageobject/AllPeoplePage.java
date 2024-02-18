package guru.qa.niffler.pageobject;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;

public class AllPeoplePage extends BasePage<AllPeoplePage> {

    private final SelenideElement allPeopleTable = $(".table tbody");

    @Step("Отображение статуса запроса в друзья: {stateActions}, к пользователю: {username}")
    public void checkCorrectDisplayPendingFriendState(String stateActions, String username) {
        allPeopleTable
                .$$("tr")
                .find(text(username))
                .$(byXpath(String.format(".//div[text()='%s']", stateActions)))
                .shouldBe(visible);
    }

    @Step("Отображение статуса, что вы друзья: {stateActions}, с пользователем : {username}")
    public void checkCorrectDisplayStateThatYouAreFriend(String username, String stateActions) {
        allPeopleTable
                .$$("tr")
                .find(text(username))
                .$(byXpath(String.format(".//div[text()='%s']", stateActions)))
                .shouldBe(visible);
    }

    @Override
    public AllPeoplePage waitForPageLoaded() {
        return null;
    }
}
