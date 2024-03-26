package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.Database;
import guru.qa.niffler.db.model.*;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

public class UserRepositoryJdbc implements UserRepository {
    private final DataSource authDs = DataSourceProvider.INSTANCE.dataSource(Database.AUTH);
    private final DataSource udDs = DataSourceProvider.INSTANCE.dataSource(Database.USERDATA);

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
                            "(username, currency, firstname, surname, photo) " +
                            "VALUES (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getCurrency().name());
                ps.setString(3, user.getFirstname());
                ps.setString(4, user.getSurname());
                ps.setObject(5, user.getPhoto());
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
    public UserAuthEntity updateUserInAuth(UserAuthEntity user) {
        try (Connection conn = authDs.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement userPs = conn.prepareStatement(
                    "UPDATE \"user\" " +
                            "SET username = ?, password = ?, enabled = ? , account_non_expired = ? , account_non_locked = ?, credentials_non_expired = ? " +
                            "WHERE id = ? ");
//                 PreparedStatement authorityPs = conn.prepareStatement(
//                         "UPDATE \"authority\" " +
//                                 "SET authority = ? " +
//                                 "WHERE user_id = ? AND authority= ? ")
                 PreparedStatement authorityPs = conn.prepareStatement(
                         "INSERT INTO \"authority\" " +
                                 "(user_id, authority) VALUES (?, ?) ");
                 PreparedStatement deleteAuthorityPs = conn.prepareStatement(
                         "DELETE FROM \"authority\" WHERE  user_id = ?"
                 )
            ) {

                userPs.setString(1, user.getUsername());
                userPs.setString(2, pe.encode(user.getPassword()));
                userPs.setBoolean(3, user.getEnabled());
                userPs.setBoolean(4, user.getAccountNonExpired());
                userPs.setBoolean(5, user.getAccountNonLocked());
                userPs.setBoolean(6, user.getCredentialsNonExpired());
                userPs.setObject(7, user.getId());

                if (userPs.executeUpdate() == 0) {
                    throw new IllegalStateException(format("Can`t find user with id : %s", user.getId()));
                }

                deleteAuthorityPs.setObject(1, user.getId());
                deleteAuthorityPs.executeUpdate();

                UUID user_id = user.getId();
                for (Authority authority : Authority.values()) {
                    authorityPs.setObject(1, user.getId());
                    authorityPs.setObject(2, authority.name());
                    authorityPs.addBatch();
                    authorityPs.clearParameters();
                }

                if (authorityPs.executeBatch().length == 0) {
                    throw new IllegalStateException(format("Can`t find user with id : %s", user.getId()));
                }
                conn.commit();

                return user;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        return userAuthEntity;

    }

    @Override
    public UserEntity updateUserInData(UserEntity user) {

        try (Connection conn = udDs.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE \"user\" " +
                            "SET username = ?, currency = ?" +
                            " WHERE id = ?")) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getCurrency().name());
                ps.setObject(3, user.getId());
                if (ps.executeUpdate() == 0) {
                    throw new IllegalStateException(format("Can`t find user with id : %s", user.getId()));
                }
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFriend(UUID targetUser , UUID friendUser, boolean pending) {
        try (Connection conn = udDs.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO \"friendship\" " +
                            "(user_id, friend_id, pending) VALUES (?, ? ,?)")) {
                ps.setObject(1, targetUser);
                ps.setObject(2, friendUser);
                ps.setBoolean(3, pending);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserAuthEntity> findByIdInAuth(UUID id) {
        List<AuthorityEntity> authorityEntityList = new ArrayList<>();
        try (Connection conn = authDs.getConnection();
             PreparedStatement usersPs = conn.prepareStatement("SELECT * " +
                     "FROM \"user\" u " +
                     "JOIN \"authority\" a ON u.id = a.user_id " +
                     "where u.id = ?")) {
            usersPs.setObject(1, id);

            usersPs.execute();
            UserAuthEntity user = null;
            boolean userProcessed = false;
            try (ResultSet resultSet = usersPs.getResultSet()) {
                while (resultSet.next()) {
                    if (!userProcessed) {
                        user = UserAuthEntity.builder()
                                .id(resultSet.getObject(1, UUID.class))
                                .username(resultSet.getString(2))
                                .password(resultSet.getString(3))
                                .enabled(resultSet.getBoolean(4))
                                .accountNonExpired(resultSet.getBoolean(5))
                                .accountNonLocked(resultSet.getBoolean(6))
                                .credentialsNonExpired(resultSet.getBoolean(7))
                                .authorities(authorityEntityList).build();
                        userProcessed = true;
                    }

                    AuthorityEntity authority = new AuthorityEntity();
                    authority.setId(resultSet.getObject(8, UUID.class));
                    authority.setAuthority(Authority.valueOf(resultSet.getString(10)));
                    user.getAuthorities().add(authority);
                }
            }
            return userProcessed ? Optional.of(user) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByIdInUserdata(UUID id) {
        UserEntity user;
        try (Connection conn = udDs.getConnection();
             PreparedStatement usersPs = conn.prepareStatement("SELECT * FROM \"user\" WHERE id = ? ")) {
            usersPs.setObject(1, id);
            usersPs.execute();
            try (ResultSet resultSet = usersPs.getResultSet()) {
                if (resultSet.next()) {
                    user = UserEntity.builder()
                            .id(resultSet.getObject("id", UUID.class))
                            .username(resultSet.getString("username"))
                            .currency(CurrencyValues.valueOf(resultSet.getString("currency")))
                            .firstname(resultSet.getString("firstname"))
                            .surname(resultSet.getString("surname"))
                            .photo(resultSet.getBytes("photo"))
                            .build();
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(user);
    }


//    private final DataSource authDs = DataSourceProvider.INSTANCE.dataSource(Database.AUTH);
//    private final DataSource udDs = DataSourceProvider.INSTANCE.dataSource(Database.USERDATA);
//
//    private final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//
//    @Step("Create user in auth")
//    @Override
//    public UserAuthEntity createInAuth(UserAuthEntity user) {
//        try (Connection conn = authDs.getConnection()) {
//            conn.setAutoCommit(false);
//
//            try (PreparedStatement userPs = conn.prepareStatement(
//                    "INSERT INTO \"user\" " +
//                            "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
//                            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
//                 PreparedStatement authorityPs = conn.prepareStatement(
//                         "INSERT INTO \"authority\" " +
//                                 "(user_id, authority) " +
//                                 "VALUES (?, ?)")
//            ) {
//
//                userPs.setString(1, user.getUsername());
//                userPs.setString(2, pe.encode(user.getPassword()));
//                userPs.setBoolean(3, user.getEnabled());
//                userPs.setBoolean(4, user.getAccountNonExpired());
//                userPs.setBoolean(5, user.getAccountNonLocked());
//                userPs.setBoolean(6, user.getCredentialsNonExpired());
//
//                userPs.executeUpdate();
//
//                UUID authUserId;
//                try (ResultSet keys = userPs.getGeneratedKeys()) {
//                    if (keys.next()) {
//                        authUserId = UUID.fromString(keys.getString("id"));
//                    } else {
//                        throw new IllegalStateException("Can`t find id");
//                    }
//                }
//
//                for (Authority authority : Authority.values()) {
//                    authorityPs.setObject(1, authUserId);
//                    authorityPs.setString(2, authority.name());
//                    authorityPs.addBatch();
//                    authorityPs.clearParameters();
//                }
//
//                authorityPs.executeBatch();
//                conn.commit();
//                user.setId(authUserId);
//            } catch (Exception e) {
//                conn.rollback();
//                throw e;
//            } finally {
//                conn.setAutoCommit(true);
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//
//            @Step("Create user in auth")
//            @Override
//            public UserAuthEntity createInAuth (UserAuthEntity user){
//                try (Connection conn = authDs.getConnection()) {
//                    conn.setAutoCommit(false);
//
//                    try (PreparedStatement userPs = conn.prepareStatement(
//                            "INSERT INTO \"user\" " +
//                                    "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
//                                    "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
//                         PreparedStatement authorityPs = conn.prepareStatement(
//                                 "INSERT INTO \"authority\" " +
//                                         "(user_id, authority) " +
//                                         "VALUES (?, ?)")
//                    ) {
//
//                        userPs.setString(1, user.getUsername());
//                        userPs.setString(2, pe.encode(user.getPassword()));
//                        userPs.setBoolean(3, user.getEnabled());
//                        userPs.setBoolean(4, user.getAccountNonExpired());
//                        userPs.setBoolean(5, user.getAccountNonLocked());
//                        userPs.setBoolean(6, user.getCredentialsNonExpired());
//
//                        userPs.executeUpdate();
//
//                        UUID authUserId;
//                        try (ResultSet keys = userPs.getGeneratedKeys()) {
//                            if (keys.next()) {
//                                authUserId = UUID.fromString(keys.getString("id"));
//                            } else {
//                                throw new IllegalStateException("Can`t find id");
//                            }
//                        }
//                        return user;
//                    }
//
//                    @Override
//                    public UserEntity createInUserdata (UserEntity user){
//                        try (Connection conn = udDs.getConnection()) {
//                            try (PreparedStatement ps = conn.prepareStatement(
//                                    "INSERT INTO \"user\" " +
//                                            "(username, currency, firstname, surname, photo) " +
//                                            "VALUES (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
//                                ps.setString(1, user.getUsername());
//                                ps.setString(2, user.getCurrency().name());
//                                ps.setString(3, user.getFirstname());
//                                ps.setString(4, user.getSurname());
//                                ps.setObject(5, user.getPhoto());
//                                ps.executeUpdate();
//
//                                UUID userId;
//                                try (ResultSet keys = ps.getGeneratedKeys()) {
//                                    if (keys.next()) {
//                                        userId = UUID.fromString(keys.getString("id"));
//                                    } else {
//                                        throw new IllegalStateException("Can`t find id");
//                                    }
//                                }
//                                user.setId(userId);
//                            }
//                        } catch (SQLException e) {
//                            throw new RuntimeException(e);
//                        }
//                        return user;
//                    }
//
//                    @Override
//                    public void deleteInAuthById (UUID id){
//
//                        try (Connection conn = authDs.getConnection()) {
//                            conn.setAutoCommit(false);
//
//                            try (PreparedStatement authorityPs = conn.prepareStatement(
//                                    "DELETE from \"authority\" " +
//                                            "WHERE user_id = ?");
//                                 PreparedStatement userPS = conn.prepareStatement(
//                                         "DELETE FROM \"user\" WHERE id = ?"
//
//                                 )) {
//                                authorityPs.setObject(1, id);
//                                userPS.setObject(1, id);
//
//                                authorityPs.executeUpdate();
//                                userPS.executeUpdate();
//
//                                conn.commit();
//
//                            } catch (Exception e) {
//                                conn.rollback();
//                                throw e;
//                            } finally {
//                                conn.setAutoCommit(true);
//                            }
//                        } catch (SQLException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//                    @Override
//                    public void deleteInUserdataById (UUID id){
//
//                        try (Connection conn = udDs.getConnection()) {
//                            try (PreparedStatement ps = conn.prepareStatement(
//                                    "DELETE FROM \"user\" " +
//                                            "WHERE id = ?")) {
//                                ps.setObject(1, id);
//                                ps.executeUpdate();
//
//                            }
//                        } catch (SQLException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }
//
//                    @Override
//                    public UserAuthEntity updateUserInAuth (UserAuthEntity user){
//                        try (Connection conn = authDs.getConnection()) {
//                            conn.setAutoCommit(false);
//
//                            try (PreparedStatement userPs = conn.prepareStatement(
//                                    "UPDATE \"user\" " +
//                                            "SET username = ?, password = ?, enabled = ? , account_non_expired = ? , account_non_locked = ?, credentials_non_expired = ? " +
//                                            "WHERE id = ? ");
////                 PreparedStatement authorityPs = conn.prepareStatement(
////                         "UPDATE \"authority\" " +
////                                 "SET authority = ? " +
////                                 "WHERE user_id = ? AND authority= ? ")
//                                 PreparedStatement authorityPs = conn.prepareStatement(
//                                         "INSERT INTO \"authority\" " +
//                                                 "(user_id, authority) VALUES (?, ?) ");
//                                 PreparedStatement deleteAuthorityPs = conn.prepareStatement(
//                                         "DELETE FROM \"authority\" WHERE  user_id = ?"
//                                 )
//                            ) {
//
//                                userPs.setString(1, user.getUsername());
//                                userPs.setString(2, pe.encode(user.getPassword()));
//                                userPs.setBoolean(3, user.getEnabled());
//                                userPs.setBoolean(4, user.getAccountNonExpired());
//                                userPs.setBoolean(5, user.getAccountNonLocked());
//                                userPs.setBoolean(6, user.getCredentialsNonExpired());
//                                userPs.setObject(7, user.getId());
//
//                                if (userPs.executeUpdate() == 0) {
//                                    throw new IllegalStateException(format("Can`t find user with id : %s", user.getId()));
//                                }
//
//                                deleteAuthorityPs.setObject(1, user.getId());
//                                deleteAuthorityPs.executeUpdate();
//
//                                UUID user_id = user.getId();
//                                for (Authority authority : Authority.values()) {
//                                    authorityPs.setObject(1, user.getId());
//                                    authorityPs.setObject(2, authority.name());
//                                    authorityPs.addBatch();
//                                    authorityPs.clearParameters();
//                                }
//
//                                if (authorityPs.executeBatch().length == 0) {
//                                    throw new IllegalStateException(format("Can`t find user with id : %s", user.getId()));
//                                }
//                                conn.commit();
//
//                                return user;
//                            } catch (Exception e) {
//                                conn.rollback();
//                                throw e;
//                            } finally {
//                                conn.setAutoCommit(true);
//                            }
//
//                        } catch (SQLException e) {
//                            throw new RuntimeException(e);
//                        }
////        return userAuthEntity;
//
//                    }
//
//                    @Override
//                    public UserEntity updateUserInData (UserEntity user){
//
//                        try (Connection conn = udDs.getConnection()) {
//                            try (PreparedStatement ps = conn.prepareStatement(
//                                    "UPDATE \"user\" " +
//                                            "SET username = ?, currency = ?" +
//                                            " WHERE id = ?")) {
//                                ps.setString(1, user.getUsername());
//                                ps.setString(2, user.getCurrency().name());
//                                ps.setObject(3, user.getId());
//                                if (ps.executeUpdate() == 0) {
//                                    throw new IllegalStateException(format("Can`t find user with id : %s", user.getId()));
//                                }
//                                return user;
//                            }
//                        } catch (SQLException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//                    @Override
//                    public Optional<UserAuthEntity> findByIdInAuth (UUID id){
//                        List<AuthorityEntity> authorityEntityList = new ArrayList<>();
//                        try (Connection conn = authDs.getConnection();
//                             PreparedStatement usersPs = conn.prepareStatement("SELECT * " +
//                                     "FROM \"user\" u " +
//                                     "JOIN \"authority\" a ON u.id = a.user_id " +
//                                     "where u.id = ?")) {
//                            usersPs.setObject(1, id);
//
//                            usersPs.execute();
//                            UserAuthEntity user = null;
//                            boolean userProcessed = false;
//                            try (ResultSet resultSet = usersPs.getResultSet()) {
//                                while (resultSet.next()) {
//                                    if (!userProcessed) {
//                                        user = UserAuthEntity.builder()
//                                                .id(resultSet.getObject(1, UUID.class))
//                                                .username(resultSet.getString(2))
//                                                .password(resultSet.getString(3))
//                                                .enabled(resultSet.getBoolean(4))
//                                                .accountNonExpired(resultSet.getBoolean(5))
//                                                .accountNonLocked(resultSet.getBoolean(6))
//                                                .credentialsNonExpired(resultSet.getBoolean(7))
//                                                .authorities(authorityEntityList).build();
//                                        userProcessed = true;
//                                    }
//
//                                    AuthorityEntity authority = new AuthorityEntity();
//                                    authority.setId(resultSet.getObject(8, UUID.class));
//                                    authority.setAuthority(Authority.valueOf(resultSet.getString(10)));
//                                    user.getAuthorities().add(authority);
//                                }
//                            }
//                            return userProcessed ? Optional.of(user) : Optional.empty();
//                        } catch (SQLException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//
//                    @Override
//                    public Optional<UserEntity> findByIdInUserdata (UUID id){
//                        UserEntity user;
//                        try (Connection conn = udDs.getConnection();
//                             PreparedStatement usersPs = conn.prepareStatement("SELECT * FROM \"user\" WHERE id = ? ")) {
//                            usersPs.setObject(1, id);
//                            usersPs.execute();
//                            try (ResultSet resultSet = usersPs.getResultSet()) {
//                                if (resultSet.next()) {
//                                    user = UserEntity.builder()
//                                            .id(resultSet.getObject("id", UUID.class))
//                                            .username(resultSet.getString("username"))
//                                            .currency(CurrencyValues.valueOf(resultSet.getString("currency")))
//                                            .firstname(resultSet.getString("firstname"))
//                                            .surname(resultSet.getString("surname"))
//                                            .photo(resultSet.getBytes("photo"))
//                                            .build();
//                                } else {
//                                    return Optional.empty();
//                                }
//                            }
//                        } catch (SQLException e) {
//                            throw new RuntimeException(e);
//                        }
//                        return Optional.of(user);
//                    }
//
//                }
//            }
//        }
//    }
}