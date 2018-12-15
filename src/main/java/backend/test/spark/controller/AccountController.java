package backend.test.spark.controller;

import backend.test.spark.logic.TransferMoneyOperation;
import spark.Request;
import spark.Response;
import spark.Route;

public class AccountController {

    private final TransferMoneyOperation transferMoneyOperation;

    public AccountController(TransferMoneyOperation transferMoneyOperation) {
        this.transferMoneyOperation = transferMoneyOperation;
    }

    public Route transferMoney() {
        return (Request request, Response response) -> transferMoneyOperation.process(request);
    }
}
