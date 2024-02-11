package guru.qa.niffler.api.category;

import guru.qa.niffler.api.RestClient;

import javax.annotation.Nonnull;

public class CategoryApiClient extends RestClient {

    public CategoryApiClient(@Nonnull String baseUri) {
        super(baseUri);
    }
}
