package backend.test.spark.dao;

import backend.test.spark.exception.InternalException;
import backend.test.spark.model.Account;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static java.lang.String.format;

public class AccountDao {

    private final JdbcConnectionPool connectionPool;

    public AccountDao(JdbcConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Optional<Account> getAccount(Long id) {
        String query = "SELECT * FROM ACCOUNTS WHERE ID = " + id;
        try (Connection connection = connectionPool.getConnection()) {
            try(Statement statement = connection.createStatement()) {
                try(ResultSet rs = statement.executeQuery(query)) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    Account account = new Account()
                            .setId(rs.getLong("ID"))
                            .setBalance(rs.getDouble("BALANCE"));
                    return Optional.of(account);
                }
            }
        } catch (SQLException e) {
            throw new InternalException(e);
        }
    }

    public void update(Account account) {
        String query =
                format("UPDATE ACCOUNTS SET BALANCE = %s WHERE ID = %d", account.getBalance().toString(), account.getId());
        try (Connection connection = connectionPool.getConnection()) {
            try(Statement statement = connection.createStatement()) {
                statement.executeUpdate(query);
            } catch (Exception e) {
                connection.rollback();
                throw new InternalException(e);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new InternalException(e);
        }
    }
}
