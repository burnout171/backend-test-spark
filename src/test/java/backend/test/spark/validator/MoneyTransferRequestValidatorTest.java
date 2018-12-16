package backend.test.spark.validator;

import backend.test.spark.DataCreator;
import backend.test.spark.exception.ValidationException;
import backend.test.spark.model.MoneyTransferRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTransferRequestValidatorTest {

    @Test
    void fromIdMissing() {
        MoneyTransferRequest givenRequest = DataCreator.givenMoneyTransferRequest();
        givenRequest.setFrom(null);

        assertThrows(ValidationException.class, () -> MoneyTransferRequestValidator.validate(givenRequest));
    }

    @Test
    void toIdMissing() {
        MoneyTransferRequest givenRequest = DataCreator.givenMoneyTransferRequest();
        givenRequest.setTo(null);

        assertThrows(ValidationException.class, () -> MoneyTransferRequestValidator.validate(givenRequest));
    }

    @Test
    void theSameIds() {
        MoneyTransferRequest givenRequest = DataCreator.givenMoneyTransferRequest();
        givenRequest.setTo(givenRequest.getFrom());

        assertThrows(ValidationException.class, () -> MoneyTransferRequestValidator.validate(givenRequest));
    }

    @Test
    void amountIsZero() {
        MoneyTransferRequest givenRequest = DataCreator.givenMoneyTransferRequest();
        givenRequest.setAmount(0D);

        assertThrows(ValidationException.class, () -> MoneyTransferRequestValidator.validate(givenRequest));
    }
}