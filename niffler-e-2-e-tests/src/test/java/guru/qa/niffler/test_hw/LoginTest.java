package guru.qa.niffler.test_hw;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.jupiter.annotations.DbUser;
import guru.qa.niffler.jupiter.extension.UserRepositoryExtension;
import guru.qa.niffler.page.WelcomePage;
import guru.qa.niffler.pageobject.MainPage;
import guru.qa.niffler.test.BaseWebTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ExtendWith(UserRepositoryExtension.class)
public class LoginTest extends BaseWebTest {

    private UserRepository userRepository;
    private UserEntity userEntity;
    private Authority userAuth;


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
        $(".main-content__section-stats").shouldBe(visible);
    }


    @Test
    void checkHibernateTest() {

        UserEntity userMork = UserEntity.builder()
                .username("БезумныйМак3с")
                .currency(CurrencyValues.RUB)
                .firstname("Хитры3й")
                .surname("Жестоки3й")
                .photo("Фото Морка".getBytes(StandardCharsets.UTF_8))
                .build();

        userRepository.createInUserdata(userMork);
    }

    @Test
    void checkUpdateUserInUserData() {

        UserEntity userMork = UserEntity.builder()
                .username("Морк")
                .currency(CurrencyValues.RUB)
                .firstname("Хитрый")
                .surname("Жестокий")
                .photo("Фото Морка".getBytes(StandardCharsets.UTF_8))
                .build();

        userRepository.createInUserdata(userMork);

        Assertions.assertEquals("Морк", userRepository.findByIdInUserdata(userMork.getId()).get().getUsername());

        UserEntity userGork = UserEntity.builder()
                .id(userMork.getId())
                .username("Горк")
                .currency(CurrencyValues.EUR)
                .firstname("Жестокий")
                .surname("Хитрый")
                .photo("Фото Горка".getBytes(StandardCharsets.UTF_8))
                .build();

        userRepository.updateUserInData(userGork);

        Assertions.assertEquals(userRepository.updateUserInData(userGork).getUsername(),
                userRepository.findByIdInUserdata(userGork.getId()).get().getUsername());

        userRepository.deleteInUserdataById(userGork.getId());

    }

    @Test
    void checkUpdateUserInUserAuth() {

        UserAuthEntity userMork = UserAuthEntity.builder()
                .username("Морк")
                .password("1234")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .authorities(Arrays.stream(Authority.values())
                        .map(e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setAuthority(e);
                            return ae;
                        }).toList()).build();

        userRepository.createInAuth(userMork);

        UserAuthEntity userGork = UserAuthEntity.builder()
                .id(userMork.getId())
                .username("Горк")
                .password("4321")
                .enabled(false)
                .accountNonExpired(false)
                .accountNonLocked(false)
                .credentialsNonExpired(false)
                .authorities(Arrays.stream(Authority.values())
                        .map(e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setAuthority(e);
                            ae.setId(userMork.getId());
                            return ae;
                        }).toList()).build();

        Assertions.assertEquals(userRepository.updateUserInAuth(userGork).getUsername(),
                userRepository.findByIdInAuth(userGork.getId()).get().getUsername());

        userRepository.deleteInAuthById(userGork.getId());

    }
}
