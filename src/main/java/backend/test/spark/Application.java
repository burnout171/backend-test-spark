package backend.test.spark;

import backend.test.spark.controller.AccountController;
import backend.test.spark.dao.AccountDao;
import backend.test.spark.logic.TransferMoneyOperation;
import backend.test.spark.util.JsonUtils;

import static spark.Spark.post;

public class Application {

    public static void main(String[] args) {
        AccountDao accountDao = new AccountDao();
        JsonUtils jsonUtils = new JsonUtils();
        TransferMoneyOperation transferMoneyOperation = new TransferMoneyOperation(jsonUtils, accountDao);
        AccountController accountController = new AccountController(transferMoneyOperation);

        post("/accounts/transfer", accountController.transferMoney());
    }

}
