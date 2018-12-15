package backend.test.spark.exception;

public class InternalException extends RuntimeException {
    public InternalException(Throwable cause) {
        super(cause);
    }
}
