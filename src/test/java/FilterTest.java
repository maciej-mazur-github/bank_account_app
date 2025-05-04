import org.junit.jupiter.api.Test;
import societegeneralekata.io.Printer;
import societegeneralekata.model.Account;
import societegeneralekata.model.Transaction;
import societegeneralekata.model.TransactionType;
import societegeneralekata.utils.Filter;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class FilterTest {

    private final Printer printer = new Printer();

    private final Account account = new Account(TestUtils.generateGeneralPreTransactions(), printer);

    @Test
    void shouldContainAllTheAccountTransactionsWhenNoTimeRangesNorTransactionTypeAreSet() {

        // given, when
        List<Transaction> transactionsBeforeFiltering = account.getTransactionsDeepCopy();
        List<Transaction> transactionsAfterFiltering = Filter.filterTransactionsByTimeRangeAndType(
                transactionsBeforeFiltering,
                null,
                null,
                null
        );

        // then
        assertThat(transactionsAfterFiltering).isEqualTo(transactionsBeforeFiltering);
    }

    @Test
    void shouldContainProperTransactionsWhenTimeRangeIsSetAndNoTransactionTypeIsProvided() {

        // given, when
        List<Transaction> transactionsBeforeFiltering = account.getTransactionsDeepCopy();
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 25, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 2, 19, 0, 0);
        List<Transaction> transactionsAfterFiltering = Filter.filterTransactionsByTimeRangeAndType(
                transactionsBeforeFiltering,
                startTime,
                endTime,
                null
        );

        // then
        assertThat(transactionsAfterFiltering.size()).isEqualTo(3);
        transactionsAfterFiltering.forEach(transaction -> assertThat(transaction).matches(
                t -> (t.getDate().isEqual(startTime) || t.getDate().isAfter(startTime))
                    &&
                        (t.getDate().isEqual(endTime) || t.getDate().isBefore(endTime))
        ));
    }

    @Test
    void shouldContainAllDepositsOnlyWhenTimeRangeIsNotSetAndTransactionTypeIsSetToDeposit() {

        // given, when
        List<Transaction> transactionsBeforeFiltering = account.getTransactionsDeepCopy();
        List<Transaction> transactionsAfterFiltering = Filter.filterTransactionsByTimeRangeAndType(
                transactionsBeforeFiltering,
                null,
                null,
                TransactionType.DEPOSIT
        );

        // then
        assertThat(transactionsAfterFiltering.size()).isEqualTo(4);
        transactionsAfterFiltering.forEach(transaction -> assertThat(transaction).matches(
                t -> t.getType() == TransactionType.DEPOSIT
        ));
    }

    @Test
    void shouldContainAllWithdrawalsOnlyWhenTimeRangeIsNotSetAndTransactionTypeIsSetToWithdrawal() {

        // given, when
        List<Transaction> transactionsBeforeFiltering = account.getTransactionsDeepCopy();
        List<Transaction> transactionsAfterFiltering = Filter.filterTransactionsByTimeRangeAndType(
                transactionsBeforeFiltering,
                null,
                null,
                TransactionType.WITHDRAWAL
        );

        // then
        assertThat(transactionsAfterFiltering.size()).isEqualTo(2);
        transactionsAfterFiltering.forEach(transaction -> assertThat(transaction).matches(
                t -> t.getType() == TransactionType.WITHDRAWAL || t.getType() == TransactionType.FULL_WITHDRAWAL
        ));
    }

    @Test
    void shouldContainAllDepositsOnlyWhenTimeRangeIsSetAndTransactionTypeIsSetToDeposit() {

        // given, when
        List<Transaction> transactionsBeforeFiltering = account.getTransactionsDeepCopy();
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 25, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 2, 19, 0, 0);
        List<Transaction> transactionsAfterFiltering = Filter.filterTransactionsByTimeRangeAndType(
                transactionsBeforeFiltering,
                startTime,
                endTime,
                TransactionType.DEPOSIT
        );

        // then
        assertThat(transactionsAfterFiltering.size()).isEqualTo(2);
        transactionsAfterFiltering.forEach(transaction -> assertThat(transaction).matches(
                t -> t.getType() == TransactionType.DEPOSIT
        ));
    }

    @Test
    void shouldNotContainAnyTransactionsWhenTimeRangeIsSetAndTransactionTypeIsSetToDepositAndNoTransactionsSuitCriteria() {

        // given, when
        List<Transaction> transactionsBeforeFiltering = account.getTransactionsDeepCopy();
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 25, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 2, 19, 0, 0);
        List<Transaction> transactionsAfterFiltering = Filter.filterTransactionsByTimeRangeAndType(
                transactionsBeforeFiltering,
                startTime,
                endTime,
                TransactionType.FULL_WITHDRAWAL
        );

        // then
        assertThat(transactionsAfterFiltering.size()).isEqualTo(0);
    }
}