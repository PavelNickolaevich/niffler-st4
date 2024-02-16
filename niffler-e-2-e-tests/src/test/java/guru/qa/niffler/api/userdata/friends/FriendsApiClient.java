package guru.qa.niffler.api.userdata.friends;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.api.spend.SpendApi;
import guru.qa.niffler.config.Config;

import javax.annotation.Nonnull;

public class FriendsApiClient extends RestClient {

    private FriendsApi friendsApi;

    public FriendsApiClient() {
        super(Config.getInstance().frontUrl());
        this.friendsApi = retrofit.create(FriendsApi.class);
    }
}
