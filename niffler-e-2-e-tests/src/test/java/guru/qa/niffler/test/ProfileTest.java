package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.jupiter.extension.UserRepositoryExtension;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.WelcomePage;
import guru.qa.niffler.page.message.SuccessMsg;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@ExtendWith(UserRepositoryExtension.class)
public class ProfileTest extends BaseWebTest {

    private UserRepository userRepository;

    private UserAuthEntity userAuth;
    private UserEntity user;


    @BeforeEach
    void createUser() {

        userAuth = UserAuthEntity.builder()
                .username("valentin_10")
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

        user = UserEntity.builder()
                .username("valentin_10")
                .currency(CurrencyValues.RUB)
                .build();

        userRepository.createInAuth(userAuth);
        userRepository.createInUserdata(user);
    }

    @AfterEach
    void removeUser() {
        userRepository.deleteInAuthById(userAuth.getId());
        userRepository.deleteInUserdataById(user.getId());
    }

    @Test
    void avatarShouldBeDisplayedInHeader() {
        Selenide.open(WelcomePage.URL, WelcomePage.class)
                .doLogin()
                .fillLoginPage(userAuth.getUsername(), userAuth.getPassword())
                .submit();

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

public class ProfileTest extends BaseWebTest {

  @DbUser
  @Test
  void avatarShouldBeDisplayedInHeader(UserAuthEntity userAuth) {
    Selenide.open(WelcomePage.URL, WelcomePage.class)
        .doLogin()
        .fillLoginPage(userAuth.getUsername(), userAuth.getPassword())
        .submit();

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
