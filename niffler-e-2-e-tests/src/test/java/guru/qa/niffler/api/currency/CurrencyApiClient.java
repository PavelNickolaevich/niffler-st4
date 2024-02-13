package guru.qa.niffler.api.currency;

import guru.qa.niffler.api.RestClient;

import javax.annotation.Nonnull;

public class CurrencyApiClient extends RestClient {

    private CurrencyApi currencyApi;

    public CurrencyApiClient(@Nonnull String baseUri) {
        super(baseUri);
        this.currencyApi = retrofit.create(CurrencyApi.class);
    }


}
