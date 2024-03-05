package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.jupiter.annotation.UserQueue;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.jupiter.extension.ContextHolderExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotations.User.UserType.*;
import static guru.qa.niffler.jupiter.annotation.UserQueue.UserType.WITH_FRIENDS;

@ExtendWith({ContextHolderExtension.class, ApiLoginExtension.class, UsersQueueExtension.class})
public class FriendsTest extends BaseWebTest {

  @Test
  @ApiLogin(username = "duck", password = "12345")
  void friendsTableShouldNotBeEmpty0(@User(WITH_FRIENDS) UserJson user) throws Exception {
    Selenide.open(FriendsPage.URL);
    System.out.println("");
  }

    @Test
    void friendsTableShouldNotBeEmpty1(@User(WITH_FRIENDS) UserJson user) throws Exception {
        Thread.sleep(3000);
    }
  @Test
  void friendsTableShouldNotBeEmpty1(@UserQueue(WITH_FRIENDS) UserJson user) throws Exception {
    Thread.sleep(3000);
  }

    @Test
    void friendsTableShouldNotBeEmpty2(@User(WITH_FRIENDS) UserJson user) throws Exception {
        Thread.sleep(3000);
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
  void friendsTableShouldNotBeEmpty2(@UserQueue(WITH_FRIENDS) UserJson user) throws Exception {
    Thread.sleep(3000);
  }
}
