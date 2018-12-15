package backend.test.spark.validator;

import backend.test.spark.exception.ValidationException;
import spark.Request;
import spark.utils.StringUtils;

public final class RequestValidator {

    private RequestValidator() {
    }

    public static void validateRequest(Request request) {
        if ("post".equalsIgnoreCase(request.requestMethod()) && StringUtils.isBlank(request.body())) {
            throw new ValidationException("Request body is missing!");
        }
    }
}
