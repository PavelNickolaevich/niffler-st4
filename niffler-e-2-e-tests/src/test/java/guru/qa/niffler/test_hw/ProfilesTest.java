package guru.qa.niffler.test_hw;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.jupiter.annotations.DbUser;
import guru.qa.niffler.jupiter.extension.UserRepositoryExtension;
import guru.qa.niffler.page.message.SuccessMsg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UserRepositoryExtension.class)
public class ProfilesTest extends  BaseWebTest{

    @BeforeEach
    void doLogin() {
        Selenide.open("http://127.0.0.1:3000/main");
        welcomePage.clickLoginButton();

    }

    @DbUser(username = "777",
            password = "787")
    @Test
    void updateNameInProfile(UserAuthEntity userAuth) throws Exception {
        loginPage
                .login(userAuth.getUsername(), userAuth.getPassword())
                .clickProfileBtn();
        profilePage
                .setName("Один")
                .submitProfile()
                .checkToasterMessage(SuccessMsg.PROFILE_UPDATED);

    }

    @DbUser
    @Test
    void updateCurrencyInProfile(UserAuthEntity userAuth) throws Exception {
        loginPage
                .login(userAuth.getUsername(), userAuth.getPassword())
                .clickProfileBtn();
        profilePage
                .selectCurrency(CurrencyValues.KZT)
                .submitProfile()
                .checkToasterMessage(SuccessMsg.PROFILE_UPDATED)
                .checkCurrentCurrencyDisplay(CurrencyValues.KZT);

    }
}
