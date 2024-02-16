package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.category.CategoryApi;
import guru.qa.niffler.api.category.CategoryApiClient;
import guru.qa.niffler.jupiter.annotations.GenerateCategory;
import guru.qa.niffler.model.CategoryJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Optional;

public class CategoryExtension implements BeforeEachCallback {

    private static final String BASE_URI = "http://127.0.0.1:8093";

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(CategoryExtension.class);

    private final CategoryApiClient categoryApiClient = new CategoryApiClient();

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {

        Optional<GenerateCategory> category = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                GenerateCategory.class
        );

        if (category.isPresent()) {

            GenerateCategory categoryData = category.get();
            CategoryJson categoryJson = new CategoryJson(
                    null,
                    categoryData.category(),
                    categoryData.username()
            );
            var createdCategory = categoryApiClient.addCategory(categoryJson);

            extensionContext.getStore(NAMESPACE)
                    .put(extensionContext.getUniqueId(), createdCategory);
        }
    }
}
