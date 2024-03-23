package guru.qa.niffler.test_hw;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.jupiter.annotations.DbUser;
import guru.qa.niffler.jupiter.annotations.GenerateCategory;
import guru.qa.niffler.jupiter.extension.UserRepositoryExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.message.SuccessMsg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.db.model.CurrencyValues.KZT;
import static guru.qa.niffler.jupiter.annotation.User.Point.OUTER;

@ExtendWith(UserRepositoryExtension.class)
public class ProfilesTest extends BaseWebTest {

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
                .selectCurrency(KZT)
                .submitProfile()
                .checkToasterMessage(SuccessMsg.PROFILE_UPDATED)
                .checkCurrentCurrencyDisplay(KZT);

    }

    @Test
    @TestUsers({
            @TestUser,
            @TestUser
    })
    @ApiLogin(user = @TestUser(
            category = {
                    @GenerateCategory(category = "Обучение3"),
                    @GenerateCategory(category = "Еда3"),
                    @GenerateCategory(category = "Вода3")
            },
            spend = {
                    @GenerateSpend(
                            username = "loki2",
                            description = "QA.GURU2",
                            amount = 72500.00,
                            category = "Угар72",
                            currency = guru.qa.niffler.model.CurrencyValues.RUB
                    )
            }
    ))
    void avatarShouldBeDisplayedInHeader(@User() UserJson user,
                                         @User(OUTER) UserJson[] outerUsers) {

        new MainPage()
                .waitForPageLoaded()
                .getHeader()
                .toProfilePage()
                .setAvatar("images/duck.jpg")
                .submitProfile()
                .checkToasterMessage(SuccessMsg.PROFILE_UPDATED);

        new MainPage()
                .getHeader()
                .checkAvatar("images/duck.jpg");
    }
}
