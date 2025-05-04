package kata.model;

import org.apache.commons.lang3.SerializationUtils;
import kata.io.Printer;
import kata.utils.Calculator;
import kata.utils.Filter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Account {
    private final List<Transaction> transactions = new ArrayList<>();
    private BigDecimal balance = BigDecimal.ZERO;
    private final Printer printer;

    // for empty account initialization
    public Account(Printer printer) {
        this.printer = printer;
    }

    // for initialization with pre-existing transactions (e.g. generated with RandomTransactionsGenerator) and for unit testing purposes
    public Account(List<PreTransaction> preTransactions, Printer printer) {
        this.printer = printer;
        performPreTransactions(preTransactions);
    }

    private void performPreTransactions(List<PreTransaction> preTransactions) {
        for (PreTransaction preTransaction : preTransactions) {
            switch (preTransaction.getType()) {
                case DEPOSIT -> makeDeposit(
                        preTransaction.getAmount(),
                        preTransaction.getDescription(),
                        preTransaction.getDate()
                );
                case WITHDRAWAL -> makeWithdrawal(
                        preTransaction.getAmount(),
                        preTransaction.getDescription(),
                        preTransaction.getDate()
                );
                case FULL_WITHDRAWAL -> withdrawAllFunds(preTransaction.getDate());
                default -> throw new RuntimeException();
            }
        }
    }

    public void makeDeposit(BigDecimal amount, String description) {
        makeDeposit(amount, description, null);
    }

    private void makeDeposit(BigDecimal amount, String description, LocalDateTime date) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            printer.printLine("Transaction amount must be greater than zero.");
            return;
        }
        balance = balance.add(amount);
        registerTransaction(amount, description, TransactionType.DEPOSIT, date);
    }

    public void makeWithdrawal(BigDecimal amount, String description) {
        makeWithdrawal(amount, description, null);
    }

    private void makeWithdrawal(BigDecimal amount, String description, LocalDateTime date) {
        if (amount.equals(BigDecimal.ZERO)) {
            printer.printLine("Transaction amount must be greater than zero.");
            return;
        }

        // In case that makeWithdrawal() is provided with negative argument and earlier stages of the program did not prevent it
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            amount = amount.negate();
        }
        if (balance.compareTo(amount) < 0) {
            printer.printLine(String.format(
                    "There are insufficient funds in your account to proceed with withdrawing requested %s EUR.",
                    amount.setScale(2, RoundingMode.HALF_DOWN))
            );
            return;
        }

        balance = balance.subtract(amount);
        registerTransaction(amount, description, TransactionType.WITHDRAWAL, date);
    }

    public void withdrawAllFunds() {
        withdrawAllFunds(null);
    }

    private void withdrawAllFunds(LocalDateTime date) {
        if (balance.equals(BigDecimal.ZERO)) {
            printer.printLine("You have no funds in your account, therefore requested withdrawal was not proceeded.");
            return;
        }

        BigDecimal transactionAmount = balance;
        balance = BigDecimal.ZERO;
        registerTransaction(transactionAmount, "Full withdrawal of funds", TransactionType.FULL_WITHDRAWAL, date);
    }

    private void registerTransaction(BigDecimal amount, String description, TransactionType type, LocalDateTime date) {
        LocalDateTime transactionDate = date == null ? LocalDateTime.now() : date;
        Transaction transaction = new Transaction(transactionDate, description, amount, balance, type);
        transactions.add(transaction);
        if (type == TransactionType.DEPOSIT) {
            printer.printLine(String.format(
                    "%s EUR has been successfully DEPOSITED on your account. Your account balance is now %s EUR.",
                    amount.setScale(2, RoundingMode.HALF_DOWN), balance.setScale(2, RoundingMode.HALF_DOWN))
            );
        } else if (type == TransactionType.WITHDRAWAL) {
            printer.printLine(String.format(
                    "%s EUR has been successfully WITHDRAWN from your account. Your account balance is now %s EUR.",
                    amount.setScale(2, RoundingMode.HALF_DOWN), balance.setScale(2, RoundingMode.HALF_DOWN))
            );
        } else {
            printer.printLine(String.format(
                    "The full withdrawal of your funds (%s EUR) has been performed. Current balance is 0 EUR.",
                    amount.setScale(2, RoundingMode.HALF_DOWN))
            );
        }
    }

    public void printAllTransactionHistory() {
        printFilteredTransactions(null, null, null);
    }

    public void printAllDeposits() {
        printFilteredTransactions(null, null, TransactionType.DEPOSIT);
    }

    private void printFilteredTransactions(LocalDate startDate, LocalDate endDate, TransactionType type) {

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (startDate != null && endDate != null) {
            startDateTime = LocalDateTime.of(startDate, LocalTime.of(0, 0));
            endDateTime = LocalDateTime.of(endDate.plusDays(1), LocalTime.of(0, 0));
        }

        List<Transaction> transactionsForPrinting = Filter.filterTransactionsByTimeRangeAndType(
                transactions,
                startDateTime,
                endDateTime,
                type);

        SumsOfTransactionsForPrinting sumsOfTransactionsForPrinting = Calculator.calculateSumsOfTransactionsForPrinting(
                balance,
                type,
                transactionsForPrinting);

        printer.printTransactionHistory(
                transactionsForPrinting,
                sumsOfTransactionsForPrinting,
                startDateTime,
                endDateTime);
    }

    public void printAllWithdrawals() {
        printFilteredTransactions(null, null, TransactionType.WITHDRAWAL);
    }

    public void printTransactionsInTimeRange(LocalDate startTime, LocalDate endTime) {
        printFilteredTransactions(startTime, endTime, null);
    }

    public void printTransactionsByTimeRangeAndType(LocalDate start, LocalDate end, TransactionType type) {
        printFilteredTransactions(start, end, type);
    }

    // for unit testing purposes
    public List<Transaction> getTransactionsDeepCopy() {
        return transactions.stream().map(SerializationUtils::clone).collect(Collectors.toList());
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
