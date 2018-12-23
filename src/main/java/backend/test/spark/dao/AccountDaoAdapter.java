package backend.test.spark.dao;

import backend.test.spark.exception.BusinessException;
import backend.test.spark.model.Account;

import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.String.format;

public class AccountDaoAdapter {

    private final AccountDao accountDao;

    public AccountDaoAdapter(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public Connection getConnection() throws SQLException {
        return accountDao.getConnection();
    }

    public Account getAccount(Connection connection, long id) throws SQLException {
        return accountDao.getAccount(connection, id)
                .orElseThrow(
                        () -> new BusinessException(
                                format("Account %d not found", id)
                        )
                );
    }

    public void update(Connection connection, Account... accounts) throws SQLException {
        accountDao.update(connection, accounts);
    }
}
