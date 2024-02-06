package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pageobject.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotations.User.UserType.WITH_FRIENDS;

public class WithParamInBeforeEachTests extends BaseWebTest {

    private final String YOU_ARE_FRIENDS = "You are friends";

    @BeforeEach
    void doLogin(@User(WITH_FRIENDS) UserJson user) {
        Selenide.open(BASE_URL, WelcomePage.class)
                .clickLoginButton()
                .login(user.username(), user.testData().password());
    }

    @Test
    void checkUserHasFriendsWithoutParam1() {
        headerFragment.
                clickFriendsBtn();
        friendsPage
                .checkCorrectDisplayStateFriend("1234", YOU_ARE_FRIENDS);
    }


    @Test
    void checkUserHasFriendsWithoutParam2() {
        headerFragment.
                clickFriendsBtn();
        friendsPage
                .checkCorrectDisplayStateFriend("duck", YOU_ARE_FRIENDS);
    }


}
