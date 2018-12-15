package backend.test.spark.logic;

import backend.test.spark.dao.AccountDao;
import backend.test.spark.exception.ValidationException;
import backend.test.spark.model.MoneyTransferRequest;
import backend.test.spark.model.Response;
import backend.test.spark.util.JsonUtils;
import backend.test.spark.validator.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

public class TransferMoneyOperation {

    private static final Logger log = LoggerFactory.getLogger(TransferMoneyOperation.class);

    private final JsonUtils jsonUtils;
    private final AccountDao accountDao;

    public TransferMoneyOperation(JsonUtils jsonUtils, AccountDao accountDao) {
        this.jsonUtils = jsonUtils;
        this.accountDao = accountDao;
    }

    public String process(Request request) {
        try {
            RequestValidator.validateRequest(request);
            return jsonUtils.toJson(internalProcess(request));
        } catch (ValidationException e) {
            log.error("Request validation fails", e);
            return jsonUtils.toJson(new Response().setSuccess(false).setMessage(e.getMessage()));
        } catch (Exception e) {
            log.error("Internal error", e);
            return jsonUtils.toJson(new Response().setSuccess(false).setMessage(e.getMessage()));
        }
    }

    private Response internalProcess(Request request) {
        MoneyTransferRequest moneyTransferRequest = jsonUtils.fromJson(request.body(), MoneyTransferRequest.class);
        //TODO
        return new Response().setSuccess(true).setMessage("ok");
    }

}
