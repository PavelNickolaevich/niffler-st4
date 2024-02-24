package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pageobject.WelcomePage;
import org.junit.jupiter.api.*;

import static guru.qa.niffler.jupiter.annotations.User.UserType.WITH_FRIENDS;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class WithParamInBeforeEachAndTestMethodTests extends BaseWebTest{

    private final String YOU_ARE_FRIENDS = "You are friends";

    @BeforeEach
    void doLogin(@User(WITH_FRIENDS) UserJson user) {
        Selenide.open(BASE_URL, WelcomePage.class)
                .clickLoginButton()
                .login(user.username(), user.testData().password());
    }

    @Test
    @Order(1)
    void checkUserHasFriendsInFriendsPage(@User(WITH_FRIENDS) UserJson user) {
        headerFragment.
                clickFriendsBtn();
        friendsPage
                .checkCorrectDisplayStateFriend("duck", YOU_ARE_FRIENDS);
    }

    @Test
    @Order(2)
    void checkUserHasFriendsInPeoplePage(@User(WITH_FRIENDS) UserJson user) {
        headerFragment.
                clickAllPeopleBtn();
        allPeoplePage
                .checkCorrectDisplayStateThatYouAreFriend("1234", YOU_ARE_FRIENDS);
    }
}
