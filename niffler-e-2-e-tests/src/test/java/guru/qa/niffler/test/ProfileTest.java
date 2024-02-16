package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.jupiter.annotations.DbUser;
import guru.qa.niffler.pageobject.ProfilePage;
import guru.qa.niffler.pageobject.WelcomePage;
import org.junit.jupiter.api.Test;

public class ProfileTest extends BaseWebTest {

    @DbUser(username = "777",
            password = "787")
    @Test
    void updateProfile(UserAuthEntity userAuth) throws Exception {
        Selenide.open("http://127.0.0.1:3000/main", WelcomePage.class)
                .clickLoginButton()
                .login(userAuth.getUsername(), userAuth.getPassword())
                .clickProfileBtn();


        profilePage
                .setName("Один")
                .setSurname("Два")
                .selectCurrency(CurrencyValues.USD)
                .addNewCategory("Три")
                .submitProfile();
    }
}
