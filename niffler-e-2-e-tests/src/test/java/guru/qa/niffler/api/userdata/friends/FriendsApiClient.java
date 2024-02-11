package guru.qa.niffler.api.userdata.friends;

import guru.qa.niffler.api.RestClient;

import javax.annotation.Nonnull;

public class FriendsApiClient extends RestClient {
    public FriendsApiClient(@Nonnull String baseUri) {
        super(baseUri);
    }
}
