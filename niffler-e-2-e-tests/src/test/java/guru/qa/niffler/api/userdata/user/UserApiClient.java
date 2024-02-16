package guru.qa.niffler.api.userdata.user;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.api.userdata.friends.FriendsApi;
import guru.qa.niffler.config.Config;

import javax.annotation.Nonnull;

public class UserApiClient extends RestClient {

    private UserApi userApi;

    public UserApiClient() {
        super(Config.getInstance().frontUrl());
        this.userApi = retrofit.create(UserApi.class);

    }
}
