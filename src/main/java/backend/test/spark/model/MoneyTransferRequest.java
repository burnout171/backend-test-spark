package backend.test.spark.model;


public class MoneyTransferRequest {
    private String from;
    private String to;
    private long amount;


    public String getFrom() {
        return from;
    }

    public MoneyTransferRequest setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getTo() {
        return to;
    }

    public MoneyTransferRequest setTo(String to) {
        this.to = to;
        return this;
    }

    public long getAmount() {
        return amount;
    }

    public MoneyTransferRequest setAmount(long amount) {
        this.amount = amount;
        return this;
    }
}
