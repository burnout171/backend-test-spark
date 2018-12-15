package backend.test.spark.dao;

import backend.test.spark.model.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountDao {

    private final Map<Long, Account> storage;

    public AccountDao() {
        storage = new HashMap<>();
        storage.put(1L, new Account().setId(1L).setBalance(10_000D));
        storage.put(2L, new Account().setId(2L).setBalance(10_000D));
    }

    public Optional<Account> getAccount(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public void save(Account account) {
        storage.put(account.getId(), account);
    }
}
