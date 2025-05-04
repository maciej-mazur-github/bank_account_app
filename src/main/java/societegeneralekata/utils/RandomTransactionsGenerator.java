package societegeneralekata.utils;

import societegeneralekata.model.PreTransaction;
import societegeneralekata.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// For generating actual random transactions pre-existing when program starts
public class RandomTransactionsGenerator {
    private static final Random random = new Random();

    public static List<PreTransaction> generateRandomPreTransactions() {
        List<PreTransaction> preTransactions = new ArrayList<>();
        int transactionsNumber = 50;
        BigDecimal transactionAmountSeed = new BigDecimal("1200");
        LocalDateTime initialTransactionDate = LocalDateTime.of(2025, 1, 12, 10, 30);

        for (int i = 1; i <= transactionsNumber; i++) {
            LocalDateTime date = generateTransactionDate(initialTransactionDate, i);
            String description = "Transaction number " + i;
            BigDecimal amount = generateRandomTransactionAmount(transactionAmountSeed);
            TransactionType type = generateRandomTransactionType();
            PreTransaction preTransaction = new PreTransaction(
                    date,
                    description,
                    amount,
                    type
            );
            preTransactions.add(preTransaction);
        }

        return preTransactions;
    }

    private static LocalDateTime generateTransactionDate(LocalDateTime initialTransactionDate, int i) {
        LocalDateTime resultDate = initialTransactionDate.plusDays(i);
        return resultDate.plusHours(i);
    }

    private static TransactionType generateRandomTransactionType() {
        int typeNumber = random.nextInt(3);
        return switch (typeNumber) {
            case 0 -> TransactionType.DEPOSIT;
            case 1 -> TransactionType.WITHDRAWAL;
            case 2 -> TransactionType.FULL_WITHDRAWAL;
            default -> throw new RuntimeException();
        };
    }

    private static BigDecimal generateRandomTransactionAmount(BigDecimal transactionAmountSeed) {
        String amountFactorString = String.valueOf(random.nextInt(10) + 1);
        BigDecimal amountFactorBigDecimal = new BigDecimal(amountFactorString);
        return amountFactorBigDecimal.multiply(transactionAmountSeed);
    }
}
