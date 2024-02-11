package guru.qa.niffler.api.userdata.user;

import guru.qa.niffler.api.RestClient;

import javax.annotation.Nonnull;

public class UserApiClient extends RestClient {
    public UserApiClient(@Nonnull String baseUri) {
        super(baseUri);
    }
}
