package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.category.CategoryApi;
import guru.qa.niffler.api.spend.SpendApi;
import guru.qa.niffler.jupiter.annotations.GenerateSpendRest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

public class RestSpendExtension extends SpendExtension implements BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(RestSpendExtension.class);

    private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private static final Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient)
            .baseUrl("http://127.0.0.1:8093")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);
    private final CategoryApi categoryApi = retrofit.create(CategoryApi.class);

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Optional<GenerateSpendRest> spend = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                GenerateSpendRest.class
        );

        if (spend.isPresent()) {
            GenerateSpendRest spendData = spend.get();

            CategoryJson categoryJson = new CategoryJson(
                    null,
                    spendData.category(),
                    spendData.username()
            );

            SpendJson spendJson = new SpendJson(
                    null,
                    new Date(),
                    spendData.category(),
                    spendData.currency(),
                    spendData.amount(),
                    spendData.description(),
                    spendData.username()
            );

            categoryApi.addCategory(categoryJson).execute();
            SpendJson created = spendApi.addSpend(spendJson).execute().body();
            extensionContext.getStore(NAMESPACE)
                    .put(extensionContext.getUniqueId(), created);
        }
    }

    @Override
    SpendJson create(SpendJson spend) {
        try {
            return spendApi.addSpend(spend).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
