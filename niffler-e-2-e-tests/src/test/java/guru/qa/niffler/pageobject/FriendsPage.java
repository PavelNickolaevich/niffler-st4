package guru.qa.niffler.pageobject;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage extends BasePage<FriendsPage> {

    private final SelenideElement friendsTable = $(".table tbody");

    @Step("Проверка, что в таблице Friends есть друг: {username} со статусом: {youAreFriends}")
    public FriendsPage checkCorrectDisplayStateFriend(String username, String actions) {
        friendsTable
                .$$("tr")
                .find(text(username))
                .$(byXpath(String.format(".//div[text()='%s']", actions)))
                .shouldBe(visible);
        return this;
    }

    @Step("Проверка, что в таблице Friends есть друг: {username}, с входящим действием: запрос в друзья")
    public FriendsPage isDisplayedActionsSubmitAndDecline(String username) {
        friendsTable
                .$$("tr")
                .find(text(username))
                .$(byXpath(".//div[@class='abstract-table__buttons']"))
                .shouldBe(visible);
        return this;
    }

    @Step("Проверка, что в таблице Friends есть друг: {username}, с кнопкой отклонения дружбы")
    public FriendsPage isDisplayedDeclineBtn(String username) {
        friendsTable
                .$$("tr")
                .find(text(username))
                .$(byXpath(".//div[@data-tooltip-id='decline-invitation']"))
                .shouldBe(visible);
        return this;
    }

    @Step("Проверка, что в таблице Friends есть пользователь: {username}, с кнопкой подтверждения дружбы")
    public FriendsPage isDisplayedActionsSubmitBtn(String username) {
        friendsTable
                .$$("tr")
                .find(text(username))
                .$(byXpath(".//div[@data-tooltip-id='submit-invitation']"))
                .shouldBe(visible);
        return this;
    }


    @Override
    public FriendsPage waitForPageLoaded() {
        return null;
    }
}
