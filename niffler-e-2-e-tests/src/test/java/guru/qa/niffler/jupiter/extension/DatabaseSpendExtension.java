package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.model.CategoryEntity;
import guru.qa.niffler.db.model.SpendEntity;
import guru.qa.niffler.db.repository.spend.SpendRepositoryJdbc;
import guru.qa.niffler.jupiter.annotations.GenerateSpend;
import guru.qa.niffler.jupiter.annotations.GenerateSpendDB;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;
import java.util.Optional;

public class DatabaseSpendExtension extends SpendExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE
            = ExtensionContext.Namespace.create(DatabaseSpendExtension.class);

    SpendRepositoryJdbc spendRepositoryJdbc = new SpendRepositoryJdbc();

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {

        Optional<GenerateSpendDB> spend = AnnotationSupport.findAnnotation(
                extensionContext.getRequiredTestMethod(),
                GenerateSpendDB.class
        );

        if (spend.isPresent()) {

            GenerateSpendDB spendData = spend.get();

            SpendJson spendJson = new SpendJson(
                    null,
                    new Date(),
                    spendData.category(),
                    spendData.currency(),
                    spendData.amount(),
                    spendData.description(),
                    spendData.username()
            );

            extensionContext.getStore(NAMESPACE)
                    .put(extensionContext.getUniqueId(), create(spendJson));
        }

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(SpendJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(DatabaseSpendExtension.NAMESPACE)
                .get(extensionContext.getUniqueId(), SpendJson.class);
    }

    @Override
    SpendJson create(SpendJson spend) {

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setUsername(spend.username());
        categoryEntity.setCategory(spend.category());

        SpendEntity spendEntity = new SpendEntity();
        spendEntity.setUsername(spend.username());
        spendEntity.setSpendDate(spend.spendDate());
        spendEntity.setCurrency(spend.currency());
        spendEntity.setAmount(spend.amount());
        spendEntity.setDescription(spend.description());
        spendEntity.setCategory(categoryEntity);

        SpendEntity spendCreated = spendRepositoryJdbc.createSpend(spendEntity);

        SpendJson spendJson = new SpendJson(
                spendCreated.getId(),
                spendCreated.getSpendDate(),
                spendCreated.getCategory().toString(),
                spendCreated.getCurrency(),
                spendCreated.getAmount(),
                spendCreated.getDescription(),
                spendCreated.getUsername()
        );
        return spendJson;
    }


}
