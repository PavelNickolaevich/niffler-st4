package guru.qa.niffler.db.repository.spend;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.model.SpendEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.UUID;

public class SpendRepositorySJdbc implements SpendRepository {

    private final TransactionTemplate spendTxt;
    private final JdbcTemplate spendTemplate;

    public SpendRepositorySJdbc() {

        JdbcTransactionManager spendTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.dataSource(Database.SPEND)
        );

        this.spendTxt = new TransactionTemplate(spendTm);
        this.spendTemplate = new JdbcTemplate(spendTm.getDataSource());
    }

    @Override
    public SpendEntity createSpend(SpendEntity spendEntity) {
        KeyHolder kh = new GeneratedKeyHolder();
        return spendTxt.execute(status -> {
            spendTemplate.update(con -> {
                PreparedStatement categoryPs = con.prepareStatement(
                        "INSERT INTO  \"category\" (category, username) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);

                categoryPs.setString(1, spendEntity.getCategory().getCategory());
                categoryPs.setString(2, spendEntity.getUsername());
                return categoryPs;
            }, kh);

            spendEntity.getCategory().setId((UUID) kh.getKeys().get("id"));

            spendTemplate.update(con -> {
                PreparedStatement spendPs = con.prepareStatement(
                        "INSERT INTO \"spend\" (username, spend_date, currency, amount, description, category_id) " +
                                "VALUES (? ,? ,?, ?, ?, ?)"
                );
                spendPs.setString(1, spendEntity.getUsername());
                spendPs.setDate(2, (Date) spendEntity.getSpendDate());
                spendPs.setString(3, spendEntity.getCurrency().name());
                spendPs.setDouble(4, spendEntity.getAmount());
                spendPs.setString(5, spendEntity.getDescription());
                spendPs.setObject(6, spendEntity.getCategory().getId());

                return spendPs;
            });
            return spendEntity;
        });
    }
}
