package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.jupiter.annotations.DbUser;
import guru.qa.niffler.jupiter.extension.UserRepositoryExtension;
import guru.qa.niffler.page.WelcomePage;
import guru.qa.niffler.pageobject.MainPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ExtendWith(UserRepositoryExtension.class)
public class LoginTest extends BaseWebTest {

    private UserRepository userRepository;
    private UserEntity userEntity;
    private UserAuthEntity userAuth;


    @BeforeEach
    void doLogin() {
        Selenide.open("http://127.0.0.1:3000/main");
        welcomePage.clickLoginButton();

    }

    @DbUser()
    @Test
    void inputIncorrectPassword(UserAuthEntity userAuth) {
        loginPage
                .loginWithWrongPassword(userAuth.getUsername(), "wrong")
                .checkErrorMessageDisplay("Неверные учетные данные пользователя");

    }

    @DbUser()
    @Test
    void inputIncorrectUserName(UserAuthEntity userAuth) {
        loginPage
                .loginWithWrongPassword("wrong", userAuth.getPassword())
                .checkErrorMessageDisplay("Неверные учетные данные пользователя");
    }

    @DbUser(username = "",
            password = "")
    @Test
    void statisticShouldBeVisibleAfterLogin(UserAuthEntity userAuth) {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue(userAuth.getUsername());
        $("input[name='password']").setValue(userAuth.getPassword());
        $("button[type='submit']").click();
        $(".main-content__section-stats").should(visible);
    }


    @BeforeEach
    void createUser() {

        userAuth = UserAuthEntity.builder()
                .username("valentin_7")
                .password("12345")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();


        AuthorityEntity[] authorities = Arrays.stream(Authority.values()).map(
                a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        userAuth.addAuthorities(authorities);

        userEntity = UserEntity.builder()
                .username("valentin_10")
                .currency(CurrencyValues.RUB)
                .build();
        userRepository.createInAuth(userAuth);
        userRepository.createInUserdata(userEntity);
    }

    @AfterEach
    void removeUser() {
        userRepository.deleteInAuthById(userAuth.getId());
        userRepository.deleteInUserdataById(userEntity.getId());
    }

    @DbUser()
    @Test
    void statisticShouldBeVisibleAfterLogin() {
        Selenide.open(WelcomePage.URL, WelcomePage.class)
                .doLogin()
                .fillLoginPage(userAuth.getUsername(), userAuth.getPassword())
                .submit();

        new MainPage()
                .waitForPageLoaded();
    }
}
