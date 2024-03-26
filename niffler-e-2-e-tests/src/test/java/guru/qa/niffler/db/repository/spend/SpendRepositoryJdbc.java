package guru.qa.niffler.db.repository.spend;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.model.SpendEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpendRepositoryJdbc implements SpendRepository {

    private final DataSource spendDs = DataSourceProvider.INSTANCE.dataSource(Database.SPEND);

    @Override
    public SpendEntity createSpend(SpendEntity spendEntity) {
        try (Connection connection = spendDs.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement category = connection.prepareStatement(
                    "INSERT INTO  \"category\" (category, username) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement spend = connection.prepareStatement(
                         "INSERT INTO \"spend\" (username, spend_date, currency, amount, description, category_id) " +
                                 "VALUES (? ,? ,?, ?, ?, ?)")
            ) {
                category.setString(1, spendEntity.getCategory().getCategory());
                category.setString(2, spendEntity.getUsername());

                category.executeUpdate();

                UUID categoryId;

                try (ResultSet keys = category.getGeneratedKeys()) {
                    if (keys.next()) {
                        categoryId = UUID.fromString(keys.getString("id"));
                    } else {
                        throw new IllegalStateException("Can`t find id");
                    }
                }

                spend.setString(1, spendEntity.getUsername());
                spend.setDate(2, (new java.sql.Date(spendEntity.getSpendDate().getTime())));
                spend.setString(3, spendEntity.getCurrency().name());
                spend.setDouble(4, spendEntity.getAmount());
                spend.setString(5, spendEntity.getDescription());
                spend.setObject(6, categoryId);

                spend.executeUpdate();
                connection.commit();
                spendEntity.getCategory().setId(categoryId);

            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spendEntity;
    }

}
