package backend.test.spark;

import backend.test.spark.controller.AccountController;
import backend.test.spark.dao.AccountDao;
import backend.test.spark.dao.AccountDaoAdapter;
import backend.test.spark.logic.TransferMoneyOperation;
import backend.test.spark.util.JsonUtils;
import org.h2.jdbcx.JdbcConnectionPool;

import static backend.test.spark.model.Constants.DB_URL;
import static backend.test.spark.model.Constants.INIT_SCRIPT;
import static java.lang.String.format;
import static spark.Spark.post;

public class Application {

    public static void main(String[] args) {
        JdbcConnectionPool connectionPool = JdbcConnectionPool.create(format("%s;%s", DB_URL, INIT_SCRIPT), "", "");
        AccountDao accountDao = new AccountDao(connectionPool);
        AccountDaoAdapter accountDaoAdapter = new AccountDaoAdapter(accountDao);
        JsonUtils jsonUtils = new JsonUtils();
        TransferMoneyOperation transferMoneyOperation = new TransferMoneyOperation(jsonUtils, accountDaoAdapter);
        AccountController accountController = new AccountController(transferMoneyOperation);
        Application.init(accountController);
    }

    public static void init(AccountController controller) {
        post("/accounts/transfer", controller.transferMoney());
    }

}
