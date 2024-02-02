package guru.qa.niffler.db.sjdbc;

import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserAuthEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class UserAuthEntityRowMapper implements RowMapper<UserAuthEntity> {

    public static final UserAuthEntityRowMapper instance = new UserAuthEntityRowMapper();

    @Override
    public UserAuthEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserAuthEntity user;

        user = UserAuthEntity.builder()
                .id(rs.getObject(1, UUID.class))
                .username(rs.getString(2))
                .password(rs.getString(3))
                .enabled(rs.getBoolean(4))
                .accountNonExpired(rs.getBoolean(5))
                .accountNonLocked(rs.getBoolean(6))
                .credentialsNonExpired(rs.getBoolean(6))
                .build();

        //??
        AuthorityEntity authority = new AuthorityEntity();
        authority.setId(rs.getObject(8, UUID.class));
        authority.setAuthority(Authority.valueOf(rs.getString(10)));
        user.getAuthorities().add(authority);

        return user;
    }
}
