package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.pageobject.AllPeoplePage;
import guru.qa.niffler.pageobject.FriendsPage;
import guru.qa.niffler.pageobject.ProfilePage;
import guru.qa.niffler.pageobject.fragment.HeaderFragment;
import guru.qa.niffler.pageobject.MainPage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.devtools.v85.profiler.model.Profile;

@ExtendWith({BrowserExtension.class})
//@ExtendWith(UsersQueueExtension.class)
public abstract class BaseWebTest {
    static {
        Configuration.browserSize = "1980x1024";
    }

    protected FriendsPage friendsPage = new FriendsPage();
    protected AllPeoplePage allPeoplePage = new AllPeoplePage();
    protected MainPage mainPage = new MainPage();
    protected HeaderFragment headerFragment = new HeaderFragment();
    protected ProfilePage profilePage = new ProfilePage();

    protected final String BASE_URL = "http://127.0.0.1:3000/main";


}
