package societegeneralekata.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// For generating actual transactions pre-existing when program starts
@AllArgsConstructor
@Getter
public class PreTransaction {
    private LocalDateTime date;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
}
