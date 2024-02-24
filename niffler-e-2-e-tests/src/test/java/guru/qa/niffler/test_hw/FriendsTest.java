package guru.qa.niffler.test_hw;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pageobject.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotations.User.UserType.*;

@ExtendWith(UsersQueueExtension.class)
public class FriendsTest extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(WITH_FRIENDS) UserJson user) {
        Selenide.open("http://127.0.0.1:3000/main", WelcomePage.class)
                .clickLoginButton();
    }

    @Test
    void checkUserHasFriends(@User(WITH_FRIENDS) UserJson user) {

        loginPage
                .login(user.username(), user.testData().password());

        headerFragment.
                clickFriendsBtn();
        friendsPage
                .checkCorrectDisplayStateFriend("loki", "You are friends");
    }

    @Test
    void checkUserHasFriendsWithoutParam() {
        headerFragment.
                clickFriendsBtn();
        friendsPage
                .checkCorrectDisplayStateFriend("123", "You are friends");
    }

    @Test
    void checkUserSendFriendRequest(@User(INVITATION_SEND) UserJson user) {

        loginPage
                .login(user.username(), user.testData().password());

        headerFragment.
                clickAllPeopleBtn();
        allPeoplePage
                .checkCorrectDisplayPendingFriendState("Pending invitation", "test");

    }

    @Test
    void checkUserReceivedFriendRequest(@User(INVITATION_RECEIVED) UserJson user) {

        loginPage
                .login(user.username(), user.testData().password());

        headerFragment.
                clickFriendsBtn();
        friendsPage
                .isDisplayedActionsSubmitBtn("duck")
                .isDisplayedDeclineBtn("duck");

    }


    @Test
    void checkUserReceivedFriendRequestWithTwoParams(@User(INVITATION_RECEIVED) UserJson user, @User(INVITATION_SEND) UserJson userRec) {

        loginPage
                .login(user.username(), user.testData().password());

        loginUser(user.username(), user.testData().password());

        headerFragment.
                clickFriendsBtn();
        friendsPage
                .isDisplayedActionsSubmitBtn(userRec.username());
    }

    private void loginUser(String username, String password) {
        Selenide.open(BASE_URL, WelcomePage.class)
                .clickLoginButton()
                .login(username, password);
    }
}
