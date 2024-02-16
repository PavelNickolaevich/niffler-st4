package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.jupiter.UserRepositoryExtension;
import guru.qa.niffler.jupiter.annotations.DbUser;
import org.junit.jupiter.api.Assertions;
import guru.qa.niffler.jupiter.annotation.DbUser;
import guru.qa.niffler.jupiter.extension.UserRepositoryExtension;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@ExtendWith(UserRepositoryExtension.class)
public class LoginTest extends BaseWebTest {

  private UserRepository userRepository;

  private UserAuthEntity userAuth;
  private UserEntity user;


  @BeforeEach
  void createUser() {
    userAuth = new UserAuthEntity();
    userAuth.setUsername("valentin_7");
    userAuth.setPassword("12345");
    userAuth.setEnabled(true);
    userAuth.setAccountNonExpired(true);
    userAuth.setAccountNonLocked(true);
    userAuth.setCredentialsNonExpired(true);

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
    AuthorityEntity[] authorities = Arrays.stream(Authority.values()).map(
        a -> {
          AuthorityEntity ae = new AuthorityEntity();
          ae.setAuthority(a);
          return ae;
        }
    ).toArray(AuthorityEntity[]::new);

    userAuth.addAuthorities(authorities);

    user = new UserEntity();
    user.setUsername("valentin_7");
    user.setCurrency(CurrencyValues.RUB);
    userRepository.createInAuth(userAuth);
    userRepository.createInUserdata(user);
  }

  @AfterEach
  void removeUser() {
    userRepository.deleteInAuthById(userAuth.getId());
    userRepository.deleteInUserdataById(user.getId());
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