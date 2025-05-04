package societegeneralekata.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

// A class to gather sum of deposits, sum of withdrawals and total balance for printing purposes
@AllArgsConstructor
@Getter
public class SumsOfTransactionsForPrinting {
    private BigDecimal sumOfDeposits;
    private BigDecimal sumOfWithdrawals;
    private BigDecimal balance;
}
