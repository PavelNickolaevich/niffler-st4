package guru.qa.niffler.db.sjdbc;


import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserAuthEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class UserAuthEntityResultSetExtractor implements ResultSetExtractor<UserAuthEntity> {

    public static final UserAuthEntityResultSetExtractor instance = new UserAuthEntityResultSetExtractor();

    @Override
    public UserAuthEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        UserAuthEntity user = null;
        List<AuthorityEntity> authorities = new ArrayList<>();
        boolean userProcessed = false;
        while (rs.next()) {
            if (!userProcessed) {
                user = UserAuthEntity.builder()
                        .id(rs.getObject(1, UUID.class))
                        .username(rs.getString(2))
                        .password(rs.getString(3))
                        .enabled(rs.getBoolean(4))
                        .accountNonExpired(rs.getBoolean(5))
                        .accountNonLocked(rs.getBoolean(6))
                        .credentialsNonExpired(rs.getBoolean(7))
                        .authorities(authorities)
                        .build();
                userProcessed = true;
            }

            AuthorityEntity authority = new AuthorityEntity();
            authority.setId(rs.getObject(8, UUID.class));
            authority.setAuthority(Authority.valueOf(rs.getString(10)));

           user.getAuthorities().add(authority);
        }

        return user;
    }
}
