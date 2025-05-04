package kata.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class Transaction implements Serializable {
    private LocalDateTime date;
    private String description;
    private BigDecimal amount;
    private BigDecimal balance;
    private TransactionType type;
}
