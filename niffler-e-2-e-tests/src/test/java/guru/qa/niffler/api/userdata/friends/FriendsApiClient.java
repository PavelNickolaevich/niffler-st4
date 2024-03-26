package guru.qa.niffler.api.userdata.friends;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.FriendJson;
import guru.qa.niffler.model.UserJson;
import retrofit2.Call;

import java.io.IOException;
import java.util.List;

public class FriendsApiClient extends RestClient {

    private FriendsApi friendsApi;

    public FriendsApiClient() {
        super(Config.getInstance().userdataUrl());
        this.friendsApi = retrofit.create(FriendsApi.class);
    }

    public UserJson addFriend(String name, FriendJson friendJson) throws IOException {
        return friendsApi.addFriend(name, friendJson).execute().body();
    }

    public List<UserJson> acceptInvitation(String username, FriendJson invitation) throws IOException {
        return friendsApi.acceptInvitation(username, invitation).execute().body();
    }
}
