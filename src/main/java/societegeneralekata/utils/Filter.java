package societegeneralekata.utils;

import societegeneralekata.model.Transaction;
import societegeneralekata.model.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public class Filter {
    public static List<Transaction> filterTransactionsByTimeRangeAndType(List<Transaction> transactions,
                                                                         LocalDateTime startDateTime,
                                                                         LocalDateTime endDateTime,
                                                                         TransactionType type) {
        List<Transaction> result;

        if (startDateTime != null && endDateTime != null) {
            result = transactions.stream()
                    .filter(
                            t -> isTransactionInTimeRange(t, startDateTime, endDateTime)
                    )
                    .toList();
        } else {
            result = transactions;
        }

        if (type != null) {
            result = result.stream()
                    .filter(t -> isTransactionOfType(t, type))
                    .toList();
        }

        return result;
    }

    public static boolean isTransactionOfType(Transaction transaction, TransactionType type) {
        if (type == TransactionType.WITHDRAWAL) {
            return transaction.getType() == TransactionType.WITHDRAWAL || transaction.getType() == TransactionType.FULL_WITHDRAWAL;
        } else {
            return transaction.getType() == type;
        }
    }

    private static boolean isTransactionInTimeRange(Transaction transaction, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return (transaction.getDate().isEqual(startDateTime) || transaction.getDate().isAfter(startDateTime))
                && (transaction.getDate().isEqual(endDateTime) || transaction.getDate().isBefore(endDateTime));
    }
}
