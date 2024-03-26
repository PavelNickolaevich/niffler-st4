package guru.qa.niffler.db.repository.category;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.model.CategoryEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;

public class CategoryRepositorySJdbc implements CategoryRepository {

    private final TransactionTemplate categoryTxt;
    private final JdbcTemplate categoryTemplate;

    public CategoryRepositorySJdbc() {

        JdbcTransactionManager categoryTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.dataSource(Database.SPEND)
        );

        this.categoryTxt = new TransactionTemplate(categoryTm);
        this.categoryTemplate = new JdbcTemplate(categoryTm.getDataSource());
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity categoryEntity) {
        KeyHolder kh = new GeneratedKeyHolder();
        return categoryTxt.execute(status -> {
            categoryTemplate.update(con -> {
                PreparedStatement categoryPs = con.prepareStatement(
                        "INSERT INTO  \"category\" (category, username) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);

                categoryPs.setString(1, categoryEntity.getCategory());
                categoryPs.setString(2, categoryEntity.getUsername());
                return categoryPs;
            }, kh);

            return categoryEntity;
        });
    }
}
