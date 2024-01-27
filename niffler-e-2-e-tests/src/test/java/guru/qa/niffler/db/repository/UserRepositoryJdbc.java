package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.JdbcUrl;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserAuthEntity;
import guru.qa.niffler.db.model.UserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRepositoryJdbc implements UserRepository {

    private final DataSource authDs = DataSourceProvider.INSTANCE.dataSource(JdbcUrl.AUTH);
    private final DataSource udDs = DataSourceProvider.INSTANCE.dataSource(JdbcUrl.USERDATA);

    private final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public UserAuthEntity createInAuth(UserAuthEntity user) {
        try (Connection conn = authDs.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement userPs = conn.prepareStatement(
                    "INSERT INTO \"user\" " +
                            "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement authorityPs = conn.prepareStatement(
                         "INSERT INTO \"authority\" " +
                                 "(user_id, authority) " +
                                 "VALUES (?, ?)")
            ) {

                userPs.setString(1, user.getUsername());
                userPs.setString(2, pe.encode(user.getPassword()));
                userPs.setBoolean(3, user.getEnabled());
                userPs.setBoolean(4, user.getAccountNonExpired());
                userPs.setBoolean(5, user.getAccountNonLocked());
                userPs.setBoolean(6, user.getCredentialsNonExpired());

                userPs.executeUpdate();

                UUID authUserId;
                try (ResultSet keys = userPs.getGeneratedKeys()) {
                    if (keys.next()) {
                        authUserId = UUID.fromString(keys.getString("id"));
                    } else {
                        throw new IllegalStateException("Can`t find id");
                    }
                }

                for (Authority authority : Authority.values()) {
                    authorityPs.setObject(1, authUserId);
                    authorityPs.setString(2, authority.name());
                    authorityPs.addBatch();
                    authorityPs.clearParameters();
                }

                authorityPs.executeBatch();
                conn.commit();
                user.setId(authUserId);
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public UserEntity createInUserdata(UserEntity user) {
        try (Connection conn = udDs.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO \"user\" " +
                            "(username, currency) " +
                            "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getCurrency().name());
                ps.executeUpdate();

                UUID userId;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        userId = UUID.fromString(keys.getString("id"));
                    } else {
                        throw new IllegalStateException("Can`t find id");
                    }
                }
                user.setId(userId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public void deleteInAuthById(UUID id) {

        try (Connection conn = authDs.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement authorityPs = conn.prepareStatement(
                    "DELETE from \"authority\" " +
                            "WHERE user_id = ?");
                 PreparedStatement userPS = conn.prepareStatement(
                         "DELETE FROM \"user\" WHERE id = ?"

                 )) {
                authorityPs.setObject(1, id);
                userPS.setObject(1, id);

                authorityPs.executeUpdate();
                userPS.executeUpdate();

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteInUserdataById(UUID id) {

        try (Connection conn = udDs.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM \"user\" " +
                            "WHERE id = ?")) {
                ps.setObject(1, id);
                ps.executeUpdate();

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public UserAuthEntity getUserFromAuth(UUID id) {
        UserAuthEntity userAuthEntity;
        try (Connection conn = authDs.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM  \"user\"" +
                            " WHERE id = ? ")) {
                ps.setObject(1, id);
                ps.executeQuery();

                try (ResultSet keys = ps.getResultSet()) {
                    if (keys.next()) {
                        userAuthEntity = UserAuthEntity.builder()
                                .id(UUID.fromString(keys.getString("id")))
                                .username(keys.getString("username"))
                                .password(keys.getString("password"))
                                .enabled(keys.getBoolean("enabled"))
                                .accountNonExpired(keys.getBoolean("account_non_expired"))
                                .accountNonLocked(keys.getBoolean("account_non_locked"))
                                .credentialsNonExpired(keys.getBoolean("credentials_non_expired"))
                                .build();
                    } else {
                        throw new IllegalStateException("Can`t find id");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userAuthEntity;
    }

    @Override
    public UserEntity getUserFromUserData(UUID id) {
        UserEntity userEntity;
        try (Connection conn = udDs.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM  \"user\"" +
                            " WHERE id = ? ")) {
                ps.setObject(1, id);
                ps.executeQuery();

                try (ResultSet keys = ps.getResultSet()) {
                    if (keys.next()) {
                        userEntity = UserEntity.builder()
                                .id(UUID.fromString(keys.getString("id")))
                                .username(keys.getString("username"))
                                .currency(keys.getObject("currency", CurrencyValues.class))
                                .build();
                    } else {
                        throw new IllegalStateException("Can`t find id");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userEntity;
    }

    @Override
    public UserAuthEntity updateUserInAuth(UserAuthEntity user) {
        UserAuthEntity userAuthEntity;
        try (Connection conn = authDs.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement userPs = conn.prepareStatement(
                    "UPDATE \"user\" " +
                            "SET username = ?, password = ?, enabled = ? , account_non_expired = ? , account_non_locked = ?, credentials_non_expired = ? " +
                            "WHERE id = ? ");
                 PreparedStatement authorityPs = conn.prepareStatement(
                         "UPDATE \"authority\" " +
                                 "SET authority = ? " +
                                 "WHERE user_id = ? ")
            ) {

                userPs.setString(1, user.getUsername());
                userPs.setString(2, pe.encode(user.getPassword()));
                userPs.setBoolean(3, user.getEnabled());
                userPs.setBoolean(4, user.getAccountNonExpired());
                userPs.setBoolean(5, user.getAccountNonLocked());
                userPs.setBoolean(6, user.getCredentialsNonExpired());
                userPs.setObject(7, user.getId());

                userPs.executeUpdate();

                try (ResultSet keys = userPs.getResultSet()) {
                    if (keys.next()) {
                        userAuthEntity = UserAuthEntity.builder()
                                .id(UUID.fromString(keys.getString("id")))
                                .username(keys.getString("username"))
                                .password(keys.getString("password"))
                                .enabled(keys.getBoolean("enabled"))
                                .accountNonExpired(keys.getBoolean("account_non_expired"))
                                .accountNonLocked(keys.getBoolean("account_non_locked"))
                                .credentialsNonExpired(keys.getBoolean("credentials_non_expired"))
                                .build();
                    } else {
                        throw new IllegalStateException("Can`t find id");
                    }
                }

                UUID user_id = user.getId();
                for (Authority authority : Authority.values()) {
                    authorityPs.setString(1, authority.name());
                    authorityPs.setObject(2, user_id);
                    authorityPs.addBatch();
                    authorityPs.clearParameters();
                }

                authorityPs.executeBatch();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userAuthEntity;

    }

    @Override
    public UserEntity updateUserInData(UserEntity user) {
        UserEntity userEntity;
        try (Connection conn = udDs.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE \"user\" " +
                            "SET username = ?, currency = ?" +
                            "WHERE id = ?")) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getCurrency().name());
                ps.setObject(3, user.getId());
                ps.executeUpdate();

                try (ResultSet keys = ps.getResultSet()) {
                    if (keys.next()) {
                        userEntity = UserEntity.builder()
                                .id(UUID.fromString(keys.getString("id")))
                                .username(keys.getString("username"))
                                .currency(keys.getObject("currency", CurrencyValues.class))
                                .build();
                    } else {
                        throw new IllegalStateException("Can`t find id");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userEntity;
    }
}
