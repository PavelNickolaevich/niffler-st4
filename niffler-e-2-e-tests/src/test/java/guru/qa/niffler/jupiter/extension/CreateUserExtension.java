package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryHibernate;
import guru.qa.niffler.jupiter.annotations.DbUser;
import guru.qa.niffler.jupiter.annotations.MyApiLogin;
import guru.qa.niffler.utils.DataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class CreateUserExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace DB_CREATE_USER_NAMESPACE
            = ExtensionContext.Namespace.create(CreateUserExtension.class);

    private static UserRepository userRepository = new UserRepositoryHibernate();


    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Optional<guru.qa.niffler.jupiter.annotations.DbUser> dbUserAnnotation = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                guru.qa.niffler.jupiter.annotations.DbUser.class
        );

        Optional<MyApiLogin> apiLoginAnnotation = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                MyApiLogin.class
        );

        if (dbUserAnnotation.isPresent() || apiLoginAnnotation.isPresent()) {
            guru.qa.niffler.jupiter.annotations.DbUser dbUser;
            if (dbUserAnnotation.isPresent()) {
                dbUser = dbUserAnnotation.get();
            } else {
                dbUser = apiLoginAnnotation.get().user();
            }
            String username = dbUser.username().isEmpty()
                    ? DataUtils.generateRandomUsername()
                    : dbUser.username();
            String password = dbUser.password().isEmpty()
                    ? "12345"
                    : dbUser.password();

            UserAuthEntity userAuth = UserAuthEntity.builder()
                    .username(username)
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
                    .username(username)
                    .currency(CurrencyValues.RUB)
                    .build();

            userRepository.createInAuth(userAuth);
            userRepository.createInUserdata(user);

            Map<String, Object> createdUser = Map.of(
                    "auth", userAuth,
                    "userdata", user
            );

            extensionContext.getStore(DB_CREATE_USER_NAMESPACE).put(extensionContext.getUniqueId(), createdUser);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        Map createdUser = extensionContext.getStore(DB_CREATE_USER_NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class);
        userRepository.deleteInAuthById(((UserAuthEntity) createdUser.get("auth")).getId());
        userRepository.deleteInUserdataById(((UserEntity) createdUser.get("userdata")).getId());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), DbUser.class)
                .isPresent() &&
                parameterContext.getParameter().getType().isAssignableFrom(UserAuthEntity.class);
    }

    @Override
    public UserAuthEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (UserAuthEntity) extensionContext.getStore(DB_CREATE_USER_NAMESPACE).get(extensionContext.getUniqueId(), Map.class)
                .get("auth");
    }
}