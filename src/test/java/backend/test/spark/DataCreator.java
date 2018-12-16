package backend.test.spark;

import backend.test.spark.model.Account;
import backend.test.spark.model.MoneyTransferRequest;

public final class DataCreator {

    public static final double AMOUNT = 100D;
    public static final long FROM_ID = 1L;
    public static final long TO_ID = 2L;

    private DataCreator() {
    }

    public static MoneyTransferRequest givenMoneyTransferRequest() {
        return givenMoneyTransferRequest(AMOUNT);
    }

    public static MoneyTransferRequest givenMoneyTransferRequest(double AMOUNT) {
        MoneyTransferRequest givenMoneyTransferRequest = new MoneyTransferRequest();
        givenMoneyTransferRequest.setFrom(FROM_ID);
        givenMoneyTransferRequest.setTo(TO_ID);
        givenMoneyTransferRequest.setAmount(AMOUNT);
        return givenMoneyTransferRequest;
    }

    public static Account givenAccount(long id, double balance) {
        return new Account().setId(id).setBalance(balance);
    }

}
