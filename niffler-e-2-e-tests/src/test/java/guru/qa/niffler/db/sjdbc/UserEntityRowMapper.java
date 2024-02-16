package guru.qa.niffler.db.sjdbc;

import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class UserEntityRowMapper implements RowMapper<UserEntity> {

  public static final UserEntityRowMapper instance = new UserEntityRowMapper();

  @Override
  public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    UserEntity user;
    user = UserEntity.builder()
            .id(rs.getObject("id", UUID.class))
            .username(rs.getString("username"))
            .currency(CurrencyValues.valueOf(rs.getString("currency")))
            .firstname(rs.getString("firstname"))
            .surname(rs.getString("surname"))
            .photo(rs.getBytes("photo"))
            .build();

    return user;
  }
}
