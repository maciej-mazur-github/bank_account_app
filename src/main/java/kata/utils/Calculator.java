package kata.utils;

import kata.model.SumsOfTransactionsForPrinting;
import kata.model.Transaction;
import kata.model.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public class Calculator {
    public static SumsOfTransactionsForPrinting calculateSumsOfTransactionsForPrinting(BigDecimal balanceForPrinting,
                                                                                       TransactionType typeForPrinting,
                                                                                       List<Transaction> transactionsForPrinting) {

        BigDecimal sumOfDeposits = null;
        BigDecimal sumOfWithdrawals = null;
        balanceForPrinting = typeForPrinting == null ? balanceForPrinting : null;

        if (typeForPrinting == TransactionType.DEPOSIT) {
            sumOfDeposits = calculateSumOfDepositsOrWithdrawals(transactionsForPrinting);
        } else if (typeForPrinting == TransactionType.WITHDRAWAL || typeForPrinting == TransactionType.FULL_WITHDRAWAL) {
            sumOfWithdrawals =  calculateSumOfDepositsOrWithdrawals(transactionsForPrinting);
        } else {
            sumOfDeposits = calculateSumOfTransactionsOfType(transactionsForPrinting, TransactionType.DEPOSIT);
            sumOfWithdrawals = calculateSumOfTransactionsOfType(transactionsForPrinting, TransactionType.WITHDRAWAL);
        }

        return new SumsOfTransactionsForPrinting(sumOfDeposits, sumOfWithdrawals, balanceForPrinting);
    }

    private static BigDecimal calculateSumOfTransactionsOfType(List<Transaction> transactionsForPrinting,
                                                               TransactionType typeForPrinting) {
        return transactionsForPrinting.stream()
                .filter(t -> Filter.isTransactionOfType(t, typeForPrinting))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateSumOfDepositsOrWithdrawals(List<Transaction> transactionsForPrinting) {
        return transactionsForPrinting.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
