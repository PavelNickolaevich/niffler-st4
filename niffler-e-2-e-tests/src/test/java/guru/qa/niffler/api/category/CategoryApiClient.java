package guru.qa.niffler.api.category;

import guru.qa.niffler.api.RestClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;

import javax.annotation.Nonnull;
import java.io.IOException;

public class CategoryApiClient extends RestClient {

    CategoryApi categoryApi;

    public CategoryApiClient() {
        super(Config.getInstance().categoryUrl());
        this.categoryApi = retrofit.create(CategoryApi.class);
    }

    public CategoryJson addCategory(CategoryJson categoryJson) throws IOException {
        return categoryApi.addCategory(categoryJson).execute().body();
    }
}
