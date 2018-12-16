package backend.test.spark.model;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class Account {
    private long id;
    private double balance;

    public long getId() {
        return id;
    }

    public Account setId(long id) {
        this.id = id;
        return this;
    }

    public double getBalance() {
        return balance;
    }

    public Account setBalance(double balance) {
        this.balance = balance;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("balance", balance)
                .toString();
    }
}
