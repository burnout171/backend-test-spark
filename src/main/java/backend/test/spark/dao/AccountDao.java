package backend.test.spark.dao;

import backend.test.spark.exception.InternalException;
import backend.test.spark.model.Account;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static java.lang.String.format;

public class AccountDao {

    private static final Logger log = LoggerFactory.getLogger(AccountDao.class);

    private final JdbcConnectionPool connectionPool;

    public AccountDao(JdbcConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Optional<Account> getAccount(long id) {
        String query = format("SELECT * FROM ACCOUNTS WHERE ID = %d FOR UPDATE", id);
        log.debug("getAccount with query = {}", query);
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
                    log.debug("update with query = {}", queryWithParameters);
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
