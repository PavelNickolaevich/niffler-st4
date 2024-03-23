package guru.qa.niffler.api.userdata.user;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;

import java.io.IOException;

public class UserApiClient extends RestClient {

    private UserApi userApi;

    public UserApiClient() {
        super(
                Config.getInstance().userUrl()
        );
        this.userApi = retrofit.create(UserApi.class);
    }

    public UserJson getUserJson(String name) throws IOException {
        return userApi
                .currentUser(name)
                .execute()
                .body();
    }
}
