package guru.qa.niffler.jupiter;

import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryJdbc;
import guru.qa.niffler.db.repository.UserRepositorySJdbc;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

@Slf4j
public class UserRepositoryExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object o, ExtensionContext extensionContext) throws Exception {
        for (Field field : o.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(UserRepository.class)) {
                field.setAccessible(true);
                field.set(o, new UserRepositoryJdbc());
                String db = System.getProperty("repository");
                switch (db) {
                    case "sjdbc" -> {
                        field.set(o, new UserRepositorySJdbc());
                        log.info("running: sjdbc");
                    }
                    case "jdbc" -> {
                        field.set(o, new UserRepositoryJdbc());
                        log.info("running: jdbc");
                    }
                    case "" -> throw new IllegalArgumentException();
                }
            }
        }
    }
}
