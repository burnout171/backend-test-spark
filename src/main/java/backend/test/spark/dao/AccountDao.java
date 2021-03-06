package backend.test.spark.dao;

import backend.test.spark.model.Account;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

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
        String query = "SELECT * FROM ACCOUNTS WHERE ID = ? FOR UPDATE";
        log.debug("getAccount with query = {}", query);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
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
        String query = "UPDATE ACCOUNTS SET BALANCE = ? WHERE ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Account account : accounts) {
                statement.setString(1, account.getBalance().toString());
                statement.setLong(2, account.getId());
                statement.executeUpdate();
            }
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
    }
}
