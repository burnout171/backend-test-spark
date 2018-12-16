package backend.test.spark.dao;

import backend.test.spark.exception.BusinessException;
import backend.test.spark.model.Account;

import static java.lang.String.format;

public class AccountDaoAdapter {

    private final AccountDao accountDao;

    public AccountDaoAdapter(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public Account getAccount(Long id) {
        return accountDao.getAccount(id)
                .orElseThrow(
                        () -> new BusinessException(
                                format("Account %d not found", id)
                        )
                );
    }

    public void update(Account account) {
        accountDao.update(account);
    }
}
