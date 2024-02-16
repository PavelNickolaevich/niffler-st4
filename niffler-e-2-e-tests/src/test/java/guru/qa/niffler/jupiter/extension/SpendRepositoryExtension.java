package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.repository.spend.SpendRepository;
import guru.qa.niffler.db.repository.spend.SpendRepositoryHibernate;
import guru.qa.niffler.db.repository.spend.SpendRepositoryJdbc;
import guru.qa.niffler.db.repository.spend.SpendRepositorySJdbc;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

@Slf4j
public class SpendRepositoryExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) throws Exception {
        for (Field field : o.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(SpendRepository.class)) {
                field.setAccessible(true);
                SpendRepository repository;
                switch (System.getProperty("repository")) {
                    case "sjdbc" -> repository = new SpendRepositorySJdbc();
                    case "jdbc" -> repository = new SpendRepositoryJdbc();
                    case "hibernate" -> repository = new SpendRepositoryHibernate();
                    default -> throw new IllegalArgumentException();
                }
                log.info(System.getProperty("repository"));
                field.set(o, repository);
            }
        }
    }
}
