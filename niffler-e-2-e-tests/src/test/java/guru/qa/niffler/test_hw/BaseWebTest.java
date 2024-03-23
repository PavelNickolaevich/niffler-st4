package guru.qa.niffler.test_hw;

import com.codeborne.selenide.Configuration;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.pageobject.*;
import guru.qa.niffler.pageobject.fragment.HeaderFragment;
import org.junit.jupiter.api.extension.ExtendWith;

//@ExtendWith({BrowserExtension.class})
@WebTest
public abstract class BaseWebTest {

    protected final String BASE_URL = "http://127.0.0.1:3000/main";

    static {
        Configuration.browserSize = "1980x1024";
    }

    protected FriendsPage friendsPage = new FriendsPage();
    protected AllPeoplePage allPeoplePage = new AllPeoplePage();
    protected MainPage mainPage = new MainPage();
    protected HeaderFragment headerFragment = new HeaderFragment();
    protected ProfilePage profilePage = new ProfilePage();
    protected WelcomePage welcomePage = new WelcomePage();
    protected LoginPage loginPage = new LoginPage();
    protected RegisterPage registerPage = new RegisterPage();

}
