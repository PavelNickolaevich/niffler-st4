package guru.qa.niffler.db.repository.category;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.model.CategoryEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryRepositoryJdbc implements CategoryRepository {

    private final DataSource categoryDs = DataSourceProvider.INSTANCE.dataSource(Database.SPEND);

    @Override
    public CategoryEntity createCategory(CategoryEntity categoryEntity) {
        try (Connection connection = categoryDs.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement category = connection.prepareStatement(
                    "INSERT INTO  \"category\" (category, username) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ) {
                category.setString(1, categoryEntity.getCategory());
                category.setString(2, categoryEntity.getUsername());

                category.executeUpdate();

                UUID categoryId;

                try (ResultSet keys = category.getGeneratedKeys()) {
                    if (keys.next()) {
                        categoryId = UUID.fromString(keys.getString("id"));
                    } else {
                        throw new IllegalStateException("Can`t find id");
                    }
                }

            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categoryEntity;
    }
}
