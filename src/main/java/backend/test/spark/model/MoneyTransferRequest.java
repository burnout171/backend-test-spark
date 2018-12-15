package backend.test.spark.model;


public class MoneyTransferRequest {
    private Long from;
    private Long to;
    private Double amount;

    public Long getFrom() {
        return from;
    }

    public MoneyTransferRequest setFrom(Long from) {
        this.from = from;
        return this;
    }

    public Long getTo() {
        return to;
    }

    public MoneyTransferRequest setTo(Long to) {
        this.to = to;
        return this;
    }

    public Double getAmount() {
        return amount;
    }

    public MoneyTransferRequest setAmount(Double amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public String toString() {
        return "MoneyTransferRequest{" +
                "from=" + from +
                ", to=" + to +
                ", amount=" + amount +
                '}';
    }
}
