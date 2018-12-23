package backend.test.spark.validator;

import backend.test.spark.exception.ValidationException;
import backend.test.spark.model.MoneyTransferRequest;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public final class MoneyTransferRequestValidator {

    private MoneyTransferRequestValidator() {
    }

    public static void validate(MoneyTransferRequest moneyTransferRequest) {
        if (isNull(moneyTransferRequest.getFrom()) || isNull(moneyTransferRequest.getTo())) {
            throw new ValidationException("One of accounts ids is missing");
        }
        if (moneyTransferRequest.getFrom().equals(moneyTransferRequest.getTo())) {
            throw new ValidationException("Charge operation under one account. Stop processing");
        }
        if (moneyTransferRequest.getAmount().doubleValue() <= 0) {
            throw new ValidationException(
                    format("Not possible to charge negative value: %f", moneyTransferRequest.getAmount())
            );
        }
    }
}
