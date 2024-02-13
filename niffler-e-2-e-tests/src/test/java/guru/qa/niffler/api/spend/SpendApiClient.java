package guru.qa.niffler.api.spend;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.api.currency.CurrencyApi;
import guru.qa.niffler.model.SpendJson;

import javax.annotation.Nonnull;
import java.io.IOException;

public class SpendApiClient extends RestClient {

    private SpendApi spendApi;

    public SpendApiClient(@Nonnull String baseUri) {
        super(baseUri);
        this.spendApi = retrofit.create(SpendApi.class);
    }

    public SpendJson addSpend(SpendJson spendJson) throws IOException {
        return spendApi.addSpend(spendJson).execute().body();
    }
}
