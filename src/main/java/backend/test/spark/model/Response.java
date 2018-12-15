package backend.test.spark.model;

public class Response {
    private Boolean success;
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public Response setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }
}
