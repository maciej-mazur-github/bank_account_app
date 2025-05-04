import kata.model.Account;
import kata.model.PreTransaction;
import kata.model.Transaction;
import kata.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TestUtils {
    public static BigDecimal calculateBalance(List<Transaction> transactions) {
        BigDecimal calculatedBalance = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            switch (transaction.getType()) {
                case DEPOSIT -> calculatedBalance = calculatedBalance.add(transaction.getAmount());
                case WITHDRAWAL -> calculatedBalance = calculatedBalance.subtract(transaction.getAmount());
                case FULL_WITHDRAWAL -> calculatedBalance = BigDecimal.ZERO;
            }
        }
        return calculatedBalance;
    }

    public static void performPreTransactions(List<PreTransaction> preTransactions, Account account) {
        for (PreTransaction preTransaction : preTransactions) {
            switch (preTransaction.getType()) {
                case DEPOSIT -> account.makeDeposit(
                        preTransaction.getAmount(),
                        preTransaction.getDescription()
                );
                case WITHDRAWAL -> account.makeWithdrawal(
                        preTransaction.getAmount(),
                        preTransaction.getDescription()
                );
                case FULL_WITHDRAWAL -> account.withdrawAllFunds();
                default -> throw new RuntimeException();
            }
        }
    }

    public static List<PreTransaction> generateValidAndInvalidPreTransactions() {
        return List.of(
                new PreTransaction(
                        LocalDateTime.of(2025, 1, 16, 10, 30),
                        "Car purchase",
                        new BigDecimal("9100"),
                        TransactionType.WITHDRAWAL),
                new PreTransaction(
                        LocalDateTime.of(2025, 1, 17, 11, 45),
                        "Full transfer to another account",
                        new BigDecimal("0"),
                        TransactionType.FULL_WITHDRAWAL),
                new PreTransaction(
                        LocalDateTime.of(2025, 1, 25, 13, 21),
                        "Laptop purchase",
                        new BigDecimal("800"),
                        TransactionType.WITHDRAWAL),
                new PreTransaction(
                        LocalDateTime.of(2025, 2, 2, 10, 30),
                        "Refund",
                        new BigDecimal("1000"),
                        TransactionType.DEPOSIT),
                new PreTransaction(
                        LocalDateTime.of(2025, 2, 18, 8, 30),
                        "Small expense",
                        new BigDecimal("0"),
                        TransactionType.WITHDRAWAL),
                new PreTransaction(
                        LocalDateTime.of(2025, 2, 2, 10, 30),
                        "Zero deposit",
                        new BigDecimal("0"),
                        TransactionType.DEPOSIT),
                new PreTransaction(
                        LocalDateTime.of(2025, 2, 2, 10, 30),
                        "Negative amount deposit",
                        new BigDecimal("-300"),
                        TransactionType.DEPOSIT),
                new PreTransaction(
                        LocalDateTime.of(2025, 2, 27, 15, 15),
                        "Negative amount withdrawal",
                        new BigDecimal("-100"),
                        TransactionType.WITHDRAWAL)
        );
    }

    public static List<PreTransaction> generateGeneralPreTransactions() {
        return List.of(
                new PreTransaction(
                        LocalDateTime.of(2025, 1, 16, 10, 30),
                        "Salary",
                        new BigDecimal("11000"),
                        TransactionType.DEPOSIT),
                new PreTransaction(
                        LocalDateTime.of(2025, 1, 17, 11, 45),
                        "Full transfer to another account",
                        new BigDecimal("0"),
                        TransactionType.FULL_WITHDRAWAL),
                new PreTransaction(
                        LocalDateTime.of(2025, 1, 25, 13, 21),
                        "Fund return",
                        new BigDecimal("800"),
                        TransactionType.DEPOSIT),
                new PreTransaction(
                        LocalDateTime.of(2025, 2, 2, 10, 30),
                        "Salary",
                        new BigDecimal("11000"),
                        TransactionType.DEPOSIT),
                new PreTransaction(
                        LocalDateTime.of(2025, 2, 18, 8, 30),
                        "Salary",
                        new BigDecimal("5000"),
                        TransactionType.WITHDRAWAL),
                new PreTransaction(
                        LocalDateTime.of(2025, 2, 27, 15, 15),
                        "Bonus for February",
                        new BigDecimal("2300"),
                        TransactionType.DEPOSIT)
        );
    }
}
