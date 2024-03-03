package guru.qa.niffler.jupiter;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.logging.JsonAllureAppender;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryJdbc;
import guru.qa.niffler.jupiter.annotations.MyApiLogin;
import guru.qa.niffler.jupiter.annotations.DbUser;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DbUserExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(DbUserExtension.class);

    private Faker faker = new Faker();
    private final UserRepository userRepository = new UserRepositoryJdbc();
    private final JsonAllureAppender jsonAllureAppender = new JsonAllureAppender();

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Optional<DbUser> dbUser = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                DbUser.class
        );

        Optional<MyApiLogin> apiLogin = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                MyApiLogin.class
        );

        Map<String, Object> userData = new HashMap<>();
        if (dbUser.isPresent() || apiLogin.isPresent()) {
            DbUser dbUserData = null;
            if (dbUser.isPresent()) {
                dbUserData = dbUser.get();
            }
            if(apiLogin.isPresent()) {
                dbUserData = apiLogin.get().user();
            }
            String userName = dbUserData.username();
            String password = dbUserData.password();
            if (userName.isEmpty() && password.isEmpty()) {
                userName = faker.name().username();
                password = faker.internet().password();
            }
            UserAuthEntity userAuth = UserAuthEntity.builder()
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

            UserEntity user = UserEntity.builder()
                    .username(userName)
                    .currency(CurrencyValues.RUB)
                    .build();

            userRepository.createInAuth(userAuth);
            userRepository.createInUserdata(user);
            jsonAllureAppender.logJson("userAuth", user);

            userData.put("userAuth", userAuth);
            userData.put("user", user);

        }

        extensionContext.getStore(NAMESPACE)
                .put(extensionContext.getUniqueId(), userData);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {

        Optional<DbUser> dbUser = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                DbUser.class
        );

        Map userData = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class);

        if (dbUser.isPresent()) {
            userRepository.deleteInAuthById(((UserAuthEntity) userData.get("userAuth")).getId());
            userRepository.deleteInUserdataById(((UserEntity) userData.get("user")).getId());
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
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class)
                .get("userAuth");
    }
}
