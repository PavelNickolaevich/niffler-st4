package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.jupiter.UsersQueueExtension;
import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.pageobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotations.User.UserType.WITH_FRIENDS;

@ExtendWith({BrowserExtension.class})
@ExtendWith(UsersQueueExtension.class)
public abstract class BaseWebTest {

    protected FriendsPage friendsPage = new FriendsPage();
    protected AllPeoplePage allPeoplePage = new AllPeoplePage();
    protected MainPage mainPage = new MainPage();
    protected HeaderFragment headerFragment = new HeaderFragment();

    protected final String BASE_URL = "http://127.0.0.1:3000/main";



}
