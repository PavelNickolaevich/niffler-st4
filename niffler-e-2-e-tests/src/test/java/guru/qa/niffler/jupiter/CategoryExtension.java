package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.SpendApi;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CategoryExtension implements BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(CategoryExtension.class);

    private static final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    private static final Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient)
            .baseUrl("http://127.0.0.1:8093")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {

        Optional<GenerateCategory> category = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                GenerateCategory.class
        );

        if (category.isPresent()) {

            GenerateCategory categoryData = category.get();
            Map<String, String> payload = new HashMap<>();
            payload.put("category", categoryData.category());
            payload.put("username", categoryData.username());

            var createdCategory = spendApi.addCategory(payload).execute().body();

            extensionContext.getStore(NAMESPACE)
                    .put("category", createdCategory);
        }
    }
}
