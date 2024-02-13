package guru.qa.niffler.api.userdata.user;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.api.userdata.friends.FriendsApi;

import javax.annotation.Nonnull;

public class UserApiClient extends RestClient {

    private UserApi userApi;

    public UserApiClient(@Nonnull String baseUri) {
        super(baseUri);
        this.userApi = retrofit.create(UserApi.class);

    }
}
