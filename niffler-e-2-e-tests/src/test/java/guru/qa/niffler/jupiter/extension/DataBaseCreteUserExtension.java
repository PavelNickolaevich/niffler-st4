package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryJdbc;
import guru.qa.niffler.db.repository.category.CategoryRepository;
import guru.qa.niffler.db.repository.category.CategoryRepositoryHibernate;
import guru.qa.niffler.db.repository.category.CategoryRepositoryJdbc;
import guru.qa.niffler.db.repository.category.CategoryRepositorySJdbc;
import guru.qa.niffler.db.repository.spend.SpendRepository;
import guru.qa.niffler.db.repository.spend.SpendRepositoryJdbc;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.jupiter.annotation.TestUser;
import guru.qa.niffler.jupiter.annotations.GenerateCategory;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.DataUtils;

import java.util.Arrays;
import java.util.Calendar;

public class DataBaseCreteUserExtension extends CreateUserExtensionLesson18 {

    private static UserRepository userRepository = new UserRepositoryJdbc();
    private static SpendRepository spendRepository = new SpendRepositoryJdbc();
    private static CategoryRepository categoryRepository = new CategoryRepositoryJdbc();
    private static CategoryRepository categoryRepositoryHibernate = new CategoryRepositoryHibernate();
    private static CategoryRepository categoryRepositorySJdbc = new CategoryRepositorySJdbc();

    @Override
    public UserJson createUser(TestUser user) {
        String username = user.username().isEmpty()
                ? DataUtils.generateRandomUsername()
                : user.username();
        String password = user.password().isEmpty()
                ? "12345"
                : user.password();

        UserAuthEntity userAuth = UserAuthEntity.builder()
                .username(username)
                .password(password)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .authorities(Arrays.stream(Authority.values())
                        .map(e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setAuthority(e);
                            return ae;
                        }).toList()).build();

        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .currency(CurrencyValues.RUB)
                .build();

        userRepository.createInAuth(userAuth);
        userRepository.createInUserdata(userEntity);

        return new UserJson(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFirstname(),
                userEntity.getSurname(),
                guru.qa.niffler.model.CurrencyValues.valueOf(userEntity.getCurrency().name()),
                userEntity.getPhoto() == null ? "" : new String(userEntity.getPhoto()),
                null,
                new TestData(
                        password,
                        null
                )
        );
    }

    @Override
    public UserJson createCategory(TestUser user, UserJson createdUser) {
        for (GenerateCategory category : user.category()) {
            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setUsername(createdUser.username());
            categoryEntity.setCategory(category.category());

            categoryRepository.createCategory(categoryEntity);
        }

        return createdUser;

    }

    @Override
    public UserJson createSpend(TestUser user, UserJson createdUser) {

        for (GenerateSpend spend : user.spend()) {

            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setUsername(spend.username());
            categoryEntity.setCategory(spend.category());

            SpendEntity spendEntity = new SpendEntity();
            spendEntity.setUsername(createdUser.username());
            spendEntity.setSpendDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
            spendEntity.setCurrency(
                    guru.qa.niffler.db.model.CurrencyValues.valueOf(spend.currency().name()));
            spendEntity.setAmount(spend.amount());
            spendEntity.setDescription(spend.description());
            spendEntity.setCategory(categoryEntity);

            spendRepository.createSpend(spendEntity);

        }

        return createdUser;
    }

}
