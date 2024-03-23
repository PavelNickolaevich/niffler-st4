
package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.category.CategoryApiClient;
import guru.qa.niffler.api.register.RegisterApiClient;
import guru.qa.niffler.api.spend.SpendApiClient;
import guru.qa.niffler.api.userdata.user.UserApiClient;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.jupiter.annotation.TestUser;
import guru.qa.niffler.jupiter.annotations.GenerateCategory;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.DataUtils;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class RestCreteUserExtension extends CreateUserExtensionLesson18 {

    private final UserApiClient userApiClient = new UserApiClient();
    private final RegisterApiClient registerApiClient = new RegisterApiClient();
    private final CategoryApiClient categoryApiClient = new CategoryApiClient();
    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public UserJson createUser(TestUser user) throws IOException {
        String username = user.username().isEmpty()
                ? DataUtils.generateRandomUsername()
                : user.username();
        String password = user.password().isEmpty()
                ? "12345"
                : user.password();

        registerApiClient.register(username, password);

        var userJson = await()
                .timeout(60, TimeUnit.SECONDS)
                .until(() -> userApiClient.getUserJson(username),
                        userName -> userName.username().equals(username));

        return new UserJson(
                userJson.id(),
                userJson.username(),
                userJson.firstname(),
                userJson.surname(),
                userJson.currency(),
                userJson.photo() == null ? "" : userJson.photo(),
                null,
                new TestData(
                        password,
                        null
                )
        );
    }

    @Override
    public UserJson createCategory(TestUser user, UserJson createdUser) throws IOException {
        for (GenerateCategory category : user.category()) {
            CategoryJson categoryJson = new CategoryJson(
                    null,
                    category.category(),
                    createdUser.username()
            );
            categoryApiClient.addCategory(categoryJson);
        }
        return createdUser;
    }

    @Override
    public UserJson createSpend(TestUser user, UserJson createdUser) throws IOException {
        for (GenerateSpend spendData : user.spend()) {
            SpendJson spendJson = new SpendJson(
                    null,
                    new Date(),
                    spendData.category(),
                    guru.qa.niffler.db.model.CurrencyValues.valueOf(spendData.currency().name()),
                    spendData.amount(),
                    spendData.description(),
                    spendData.username()
            );
            spendApiClient.addSpend(spendJson);
        }
        return createdUser;
    }
}
