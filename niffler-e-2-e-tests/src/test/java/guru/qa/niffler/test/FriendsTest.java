package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pageobject.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotations.User.UserType.*;

public class FriendsTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(WITH_FRIENDS) UserJson user) {
        Selenide.open("http://127.0.0.1:3000/main", WelcomePage.class)
                .clickLoginButton()
                .login(user.username(), user.testData().password());
    }

    @Test
    void friendsTableShouldNotBeEmpty0(@User(WITH_FRIENDS) UserJson user) throws Exception {
        Thread.sleep(3000);
    }

    @Test
    void friendsTableShouldNotBeEmpty1(@User(WITH_FRIENDS) UserJson user) throws Exception {
        Thread.sleep(3000);
    }

    @Test
    void friendsTableShouldNotBeEmpty2(@User(WITH_FRIENDS) UserJson user) throws Exception {
        Thread.sleep(3000);
    }

    @Test
    void checkUserHasFriends(@User(WITH_FRIENDS) UserJson user) {
        headerFragment.
                clickFriendsBtn();
        friendsPage
                .checkCorrectDisplayStateFriend("1234", "You are friends");
    }

    @Test
    void checkUserHasFriendsWithoutParam() {
        headerFragment.
                clickFriendsBtn();
        friendsPage
                .checkCorrectDisplayStateFriend("1234", "You are friends");
    }

    @Test
    void checkUserSendFriendRequest(@User(INVITATION_SEND) UserJson user) {
        headerFragment.
                clickAllPeopleBtn();
        allPeoplePage
                .checkCorrectDisplayPendingFriendState("Pending invitation", "thor");

    }

    @Test
    void checkUserReceivedFriendRequest(@User(INVITATION_RECEIVED) UserJson user) {
        headerFragment.
                clickFriendsBtn();
        friendsPage
                .isDisplayedActionsSubmitBtn("loki")
                .isDisplayedDeclineBtn("loki");
    }
}
