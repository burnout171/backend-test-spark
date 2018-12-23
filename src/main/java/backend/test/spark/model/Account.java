package backend.test.spark.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class Account {
    private long id;
    private BigDecimal balance;
}
