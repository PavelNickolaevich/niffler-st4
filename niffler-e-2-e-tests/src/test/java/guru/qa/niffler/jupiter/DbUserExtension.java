package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryJdbc;
import guru.qa.niffler.jupiter.annotations.DbUser;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Arrays;
import java.util.Optional;

@ExtendWith(UserRepositoryExtension.class)
public class DbUserExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(DbUserExtension.class);

    private Faker faker = new Faker();
    private UserAuthEntity userAuth;
    private UserEntity user;
    private UserRepository userRepository;

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Optional<DbUser> dbUser = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                DbUser.class
        );

        if (dbUser.isPresent()) {
            DbUser dbUserData = dbUser.get();
            String userName = dbUserData.username();
            String password = dbUserData.password();
            if (userName.isEmpty() && password.isEmpty()) {
                userName = faker.name().username();
                password = faker.internet().password();
            }
            userAuth = UserAuthEntity.builder()
                    .username(userName)
                    .password(password)
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

            user = UserEntity.builder()
                    .username(userName)
                    .currency(CurrencyValues.RUB)
                    .build();

            userRepository = new UserRepositoryJdbc();
            userRepository.createInAuth(userAuth);
            userRepository.createInUserdata(user);

            extensionContext.getStore(NAMESPACE)
                    .put("userAuth", userAuth);

        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserAuthEntity.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(DbUserExtension.NAMESPACE)
                .get("userAuth", UserAuthEntity.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {

        Optional<DbUser> dbUser = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                DbUser.class
        );

        if (dbUser.isPresent()) {
            userRepository.deleteInAuthById(userAuth.getId());
            userRepository.deleteInUserdataById(user.getId());
        }
    }
}
