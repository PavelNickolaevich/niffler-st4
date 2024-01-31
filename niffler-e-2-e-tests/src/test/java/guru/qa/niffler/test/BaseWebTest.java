package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.BrowserExtension;
import guru.qa.niffler.pageobject.AllPeoplePage;
import guru.qa.niffler.pageobject.FriendsPage;
import guru.qa.niffler.pageobject.HeaderFragment;
import guru.qa.niffler.pageobject.MainPage;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({BrowserExtension.class})
//@ExtendWith(UsersQueueExtension.class)
public abstract class BaseWebTest {

    protected FriendsPage friendsPage = new FriendsPage();
    protected AllPeoplePage allPeoplePage = new AllPeoplePage();
    protected MainPage mainPage = new MainPage();
    protected HeaderFragment headerFragment = new HeaderFragment();

    protected final String BASE_URL = "http://127.0.0.1:3000/main";


}
