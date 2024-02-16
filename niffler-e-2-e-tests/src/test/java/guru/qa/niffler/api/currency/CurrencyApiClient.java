package guru.qa.niffler.api.currency;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.config.Config;

import javax.annotation.Nonnull;

public class CurrencyApiClient extends RestClient {

    private CurrencyApi currencyApi;

    public CurrencyApiClient() {
        super(Config.getInstance().frontUrl());
        this.currencyApi = retrofit.create(CurrencyApi.class);
    }


}
