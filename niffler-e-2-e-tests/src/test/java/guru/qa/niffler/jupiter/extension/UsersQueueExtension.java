package guru.qa.niffler.jupiter.extension;


import guru.qa.niffler.jupiter.annotations.User;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static guru.qa.niffler.jupiter.annotations.User.UserType.*;


public class UsersQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    private static Map<User.UserType, Queue<UserJson>> users = new ConcurrentHashMap<>();

    static {
        Queue<UserJson> friendsQueue = new ConcurrentLinkedQueue<>();
        Queue<UserJson> commonQueue = new ConcurrentLinkedQueue<>();
        Queue<UserJson> invitationSendQueue = new ConcurrentLinkedQueue<>();
        Queue<UserJson> receivedQueue = new ConcurrentLinkedQueue<>();

        friendsQueue.add(user("duck", "12345", WITH_FRIENDS));
        friendsQueue.add(user("1234", "1234", WITH_FRIENDS));

        commonQueue.add(user("bee", "12345", COMMON));
        commonQueue.add(user("barsik", "12345", COMMON));

        invitationSendQueue.add(user("loki", "12345", INVITATION_SEND));
        invitationSendQueue.add(user("thor", "12345", INVITATION_RECEIVED));


        receivedQueue.add(user("thor", "12345", INVITATION_RECEIVED));

        users.put(WITH_FRIENDS, friendsQueue);
        users.put(COMMON, commonQueue);
        users.put(INVITATION_SEND, invitationSendQueue);
        users.put(INVITATION_RECEIVED, receivedQueue);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Map<User.UserType, UserJson> testCandidates = new HashMap<>();

        List<Parameter> parametersFromTest = Arrays.stream(context.getRequiredTestMethod().getParameters())
                .collect(Collectors.toList());

        List<Parameter> parametersFromBeforeEach = Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class))
                .map(param -> param.getParameters()).flatMap(array -> Arrays.stream(array))
                .collect(Collectors.toList());

        List<Parameter> allParam = new ArrayList<>();
        allParam.addAll(parametersFromBeforeEach);
        allParam.addAll(parametersFromTest);

        List<Parameter> filterParam = allParam.stream()
                .filter(parameter -> parameter.getType().isAssignableFrom(UserJson.class) && parameter.getAnnotation(User.class) != null)
                .collect(Collectors.toList());

        for (Parameter parameter : filterParam) {
            User annotation = parameter.getAnnotation(User.class);
            UserJson testCandidate = null;
            Queue<UserJson> queue = users.get(annotation.value());
            while (testCandidate == null) {
                testCandidate = queue.poll();
            }
            testCandidates.put(testCandidate.testData().userType(), testCandidate);
            context.getStore(NAMESPACE).put(context.getUniqueId(), testCandidates);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Map<User.UserType, UserJson> usersFromTest = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        for (User.UserType userType : usersFromTest.keySet()) {
            users.get(userType).add(usersFromTest.get(userType));
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserJson.class) &&
                parameterContext.getParameter().isAnnotationPresent(User.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (UserJson) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class)
                .get(parameterContext.findAnnotation(User.class).get().value());
    }

    private static UserJson user(String username, String password, User.UserType userType) {
        return new UserJson(
                null,
                username,
                null,
                null,
                CurrencyValues.RUB,
                null,
                null,
                new TestData(
                        password,
                        userType
                )
        );
    }
}
