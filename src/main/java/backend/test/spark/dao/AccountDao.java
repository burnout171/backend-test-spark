package backend.test.spark.dao;

import backend.test.spark.model.Account;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
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

    public Connection getConnection() throws SQLException {
            Connection connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            return connection;
    }

    public Optional<Account> getAccount(Connection connection, long id) throws SQLException {
        String query = format("SELECT * FROM ACCOUNTS WHERE ID = %d FOR UPDATE", id);
        log.debug("getAccount with query = {}", query);
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery(query)) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Account account = new Account()
                        .setId(rs.getLong("ID"))
                        .setBalance(new BigDecimal(rs.getString("BALANCE")));
                return Optional.of(account);
            }
        }
    }

    public void update(Connection connection, Account... accounts) throws SQLException {
        String query = "UPDATE ACCOUNTS SET BALANCE = %s WHERE ID = %d";
        try (Statement statement = connection.createStatement()) {
            for (Account account : accounts) {
                String queryWithParameters = format(query, account.getBalance(), account.getId());
                log.debug("update with query = {}", queryWithParameters);
                statement.executeUpdate(queryWithParameters);
            }
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
    }
}
