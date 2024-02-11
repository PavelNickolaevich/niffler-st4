package guru.qa.niffler.api.currency;

import guru.qa.niffler.api.RestClient;

import javax.annotation.Nonnull;

public class CurrencyApiClient extends RestClient {
    public CurrencyApiClient(@Nonnull String baseUri) {
        super(baseUri);
    }
}
