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

    public Optional<Account> getAccount(long id) {
        String query = format("SELECT * FROM ACCOUNTS WHERE ID = %d FOR UPDATE", id);
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery(query)) {
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

    public void update(Account... accounts) {
        String query = "UPDATE ACCOUNTS SET BALANCE = %s WHERE ID = %d";
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                for (Account account : accounts) {
                    String queryWithParameters = format(query, account.getBalance(), account.getId());
                    statement.executeUpdate(queryWithParameters);
                }
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
