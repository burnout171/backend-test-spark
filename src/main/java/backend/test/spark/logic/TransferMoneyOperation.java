package backend.test.spark.logic;

import backend.test.spark.dao.AccountDaoAdapter;
import backend.test.spark.exception.BusinessException;
import backend.test.spark.exception.InternalException;
import backend.test.spark.exception.ValidationException;
import backend.test.spark.model.Account;
import backend.test.spark.model.MoneyTransferRequest;
import backend.test.spark.model.Response;
import backend.test.spark.util.JsonUtils;
import backend.test.spark.validator.MoneyTransferRequestValidator;
import backend.test.spark.validator.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class TransferMoneyOperation {

    private static final Logger log = LoggerFactory.getLogger(TransferMoneyOperation.class);

    private final JsonUtils jsonUtils;
    private final AccountDaoAdapter accountDaoAdapter;

    public TransferMoneyOperation(JsonUtils jsonUtils, AccountDaoAdapter accountDaoAdapter) {
        this.jsonUtils = jsonUtils;
        this.accountDaoAdapter = accountDaoAdapter;
    }

    public String process(Request request) {
        log.info("process.in");
        try {
            String response = jsonUtils.toJson(internalProcess(request));
            log.info("process.out");
            return response;
        } catch (ValidationException e) {
            log.error("Request validation fails", e);
            return jsonUtils.toJson(new Response().setSuccess(false).setMessage(e.getMessage()));
        } catch (BusinessException e) {
            log.error("Wrong data", e);
            return jsonUtils.toJson(new Response().setSuccess(false).setMessage(e.getMessage()));
        } catch (Exception e) {
            log.error("Internal error", e);
            return jsonUtils.toJson(new Response().setSuccess(false).setMessage(e.getMessage()));
        }
    }

    private Response internalProcess(Request request) {
        MoneyTransferRequest moneyTransferRequest = bodyToObject(request);
        try (Connection connection = accountDaoAdapter.getConnection()) {
            Account fromAccount = accountDaoAdapter.getAccount(connection, moneyTransferRequest.getFrom());
            Account toAccount = accountDaoAdapter.getAccount(connection, moneyTransferRequest.getTo());
            doTransfer(connection, fromAccount, toAccount, moneyTransferRequest.getAmount());
            connection.commit();
        } catch (SQLException e) {
            throw new InternalException(e);
        }
        return new Response().setSuccess(true).setMessage("done");
    }

    private MoneyTransferRequest bodyToObject(Request request) {
        RequestValidator.validateRequest(request);
        MoneyTransferRequest moneyTransferRequest = jsonUtils.fromJson(request.body(), MoneyTransferRequest.class);
        MoneyTransferRequestValidator.validate(moneyTransferRequest);
        return moneyTransferRequest;
    }

    private void doTransfer(Connection connection, Account fromAccount, Account toAccount, BigDecimal amount)
            throws SQLException {
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new ValidationException("Not enough money!");
        }
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountDaoAdapter.update(connection, fromAccount, toAccount);
    }
}
