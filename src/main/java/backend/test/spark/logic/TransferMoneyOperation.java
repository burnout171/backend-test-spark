package backend.test.spark.logic;

import backend.test.spark.dao.AccountDao;
import backend.test.spark.exception.BusinessException;
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

import static java.lang.String.format;

public class TransferMoneyOperation {

    private static final Logger log = LoggerFactory.getLogger(TransferMoneyOperation.class);

    private final JsonUtils jsonUtils;
    private final AccountDao accountDao;

    public TransferMoneyOperation(JsonUtils jsonUtils, AccountDao accountDao) {
        this.jsonUtils = jsonUtils;
        this.accountDao = accountDao;
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
        long fromId = moneyTransferRequest.getFrom();
        long toId = moneyTransferRequest.getTo();
        Account fromAccount = accountDao.getAccount(fromId)
                .orElseThrow(
                        () -> new BusinessException(
                                format("From account %d not found", fromId)
                        )
                );
        Account toAccount = accountDao.getAccount(toId)
                .orElseThrow(
                        () -> new BusinessException(
                                format("To account %d not found", toId)
                        )
                );
        doTransfer(fromAccount, toAccount, moneyTransferRequest.getAmount());
        return new Response().setSuccess(true).setMessage("done");
    }

    private MoneyTransferRequest bodyToObject(Request request) {
        RequestValidator.validateRequest(request);
        MoneyTransferRequest moneyTransferRequest = jsonUtils.fromJson(request.body(), MoneyTransferRequest.class);
        MoneyTransferRequestValidator.validate(moneyTransferRequest);
        return moneyTransferRequest;
    }

    private void doTransfer(Account fromAccount, Account toAccount, Double amount) {
        if (fromAccount.getBalance().compareTo(amount) < 0)
            throw new ValidationException("Not enough money!");
        else {
            fromAccount.setBalance( fromAccount.getBalance() - amount );
            toAccount.setBalance( toAccount.getBalance() + amount);
            accountDao.save(fromAccount);
            accountDao.save(toAccount);
        }
    }
}
