package guru.qa.niffler.test_hw;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pageobject.WelcomePage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotations.User.UserType.*;

public class WithParamInTestMethodOnlyTests extends BaseWebTest {

    private final String YOU_ARE_FRIENDS = "You are friends";
    private final String PENDING_INVITATION = "Pending invitation";

    @Test
    void checkUserHasFriends(@User(WITH_FRIENDS) UserJson user) {
        loginUser(user.username(), user.testData().password());

        headerFragment.
                clickFriendsBtn();
        friendsPage
                .checkCorrectDisplayStateFriend("1234", YOU_ARE_FRIENDS);
    }

    @Test
    void checkUserSendFriendRequest(@User(INVITATION_SEND) UserJson user) {
        loginUser(user.username(), user.testData().password());

        headerFragment.
                clickAllPeopleBtn();
        allPeoplePage
                .checkCorrectDisplayPendingFriendState(PENDING_INVITATION, "thor");

    }

    @Test
    void checkUserReceivedFriendRequest(@User(INVITATION_RECEIVED) UserJson user) {
        loginUser(user.username(), user.testData().password());

        headerFragment.
                clickFriendsBtn();
        friendsPage
                .isDisplayedActionsSubmitBtn("loki")
                .isDisplayedDeclineBtn("loki");
    }

    @Test
    void checkUserReceivedFriendRequestWithTwoParams(@User(INVITATION_RECEIVED) UserJson user, @User(INVITATION_SEND) UserJson userRec) {
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
