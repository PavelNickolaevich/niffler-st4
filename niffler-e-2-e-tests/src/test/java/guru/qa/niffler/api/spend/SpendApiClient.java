package guru.qa.niffler.api.spend;

import guru.qa.niffler.api.RestClient;

import javax.annotation.Nonnull;

public class SpendApiClient extends RestClient {

    public SpendApiClient(@Nonnull String baseUri) {
        super(baseUri);
    }
}
