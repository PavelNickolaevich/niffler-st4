package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.category.CategoryApiClient;
import guru.qa.niffler.api.spend.SpendApiClient;
import guru.qa.niffler.jupiter.annotations.GenerateSpendRest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

public class RestSpendExtension extends SpendExtension implements BeforeEachCallback {

    private static final String BASE_URI = "http://127.0.0.1:8093";

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(RestSpendExtension.class);

    private final CategoryApiClient categoryApiClient = new CategoryApiClient();
    private final SpendApiClient spendApiClient = new SpendApiClient();

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

            categoryApiClient.addCategory(categoryJson);
            SpendJson created = spendApiClient.addSpend(spendJson);
            extensionContext.getStore(NAMESPACE)
                    .put(extensionContext.getUniqueId(), created);
        }
    }

    @Override
    SpendJson create(SpendJson spend) {
        try {
            return spendApiClient.addSpend(spend);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
