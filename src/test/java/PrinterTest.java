import org.junit.jupiter.api.Test;
import societegeneralekata.io.Printer;
import societegeneralekata.model.Account;
import societegeneralekata.model.SumsOfTransactionsForPrinting;
import societegeneralekata.model.Transaction;
import societegeneralekata.model.TransactionType;
import societegeneralekata.utils.Calculator;
import societegeneralekata.utils.Filter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PrinterTest {

    private final Printer printer = new Printer();

    private final Account account = new Account(TestUtils.generateGeneralPreTransactions(), printer);

    @Test
    void shouldPrintProperDetailsWhenFullTransactionHistoryIsRequested() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        List<String> headerColumnNames = List.of("DATE", "DESCRIPTION", "DEPOSIT", "WITHDRAWAL", "BALANCE");
        List<String> allTransactionsAmounts = account.getTransactionsDeepCopy().stream()
                .map(Transaction::getAmount)
                .map(BigDecimal::toString)
                .toList();
        SumsOfTransactionsForPrinting sumsOfTransactionsForPrinting = Calculator.calculateSumsOfTransactionsForPrinting(
                account.getBalance(),
                null,
                account.getTransactionsDeepCopy()
        );

        // when
        account.printAllTransactionHistory();

        // then
        headerColumnNames.forEach(header -> assertThat(outputStream.toString()).contains(header));
        allTransactionsAmounts.forEach(amount -> assertThat(outputStream.toString()).contains(amount));
        assertThat(outputStream.toString()).contains(sumsOfTransactionsForPrinting.getSumOfDeposits().toString());
        assertThat(outputStream.toString()).contains(sumsOfTransactionsForPrinting.getSumOfWithdrawals().toString());
        assertThat(outputStream.toString()).contains(sumsOfTransactionsForPrinting.getBalance().toString());

        System.setOut(originalOut);
    }

    @Test
    void shouldPrintProperDetailsWhenDepositHistoryOnlyIsRequested() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        List<String> expectedHeaderColumnNames = List.of("DATE", "DESCRIPTION", "DEPOSIT");
        List<String> undesiredHeaderColumnNames = List.of("WITHDRAWAL", "BALANCE");
        List<Transaction> allDeposits = Filter.filterTransactionsByTimeRangeAndType(
                account.getTransactionsDeepCopy(),
                null,
                null,
                TransactionType.DEPOSIT);
        List<String> allDepositsAmounts = allDeposits.stream()
                .map(Transaction::getAmount)
                .map(BigDecimal::toString)
                .toList();
        SumsOfTransactionsForPrinting sumsOfTransactionsForPrinting = Calculator.calculateSumsOfTransactionsForPrinting(
                account.getBalance(),
                TransactionType.DEPOSIT,
                allDeposits
        );

        // when
        account.printAllDeposits();

        // then
        expectedHeaderColumnNames.forEach(header -> assertThat(outputStream.toString()).contains(header));
        undesiredHeaderColumnNames.forEach(header -> assertThat(outputStream.toString()).doesNotContain(header));
        allDepositsAmounts.forEach(amount -> assertThat(outputStream.toString()).contains(amount));
        assertThat(outputStream.toString()).contains(sumsOfTransactionsForPrinting.getSumOfDeposits().toString());

        System.setOut(originalOut);
    }

    @Test
    void shouldPrintProperDetailsWhenWithdrawalHistoryOnlyIsRequested() {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        List<String> expectedHeaderColumnNames = List.of("DATE", "DESCRIPTION", "WITHDRAWAL");
        List<String> undesiredHeaderColumnNames = List.of("BALANCE", "DEPOSIT");
        List<Transaction> allWithdrawals = Filter.filterTransactionsByTimeRangeAndType(
                account.getTransactionsDeepCopy(),
                null,
                null,
                TransactionType.WITHDRAWAL);
        List<String> allWithdrawalsAmounts = allWithdrawals.stream()
                .map(Transaction::getAmount)
                .map(BigDecimal::toString)
                .toList();
        SumsOfTransactionsForPrinting sumsOfTransactionsForPrinting = Calculator.calculateSumsOfTransactionsForPrinting(
                account.getBalance(),
                TransactionType.WITHDRAWAL,
                allWithdrawals
        );

        // when
        account.printAllWithdrawals();

        // then
        expectedHeaderColumnNames.forEach(header -> assertThat(outputStream.toString()).contains(header));
        undesiredHeaderColumnNames.forEach(header -> assertThat(outputStream.toString()).doesNotContain(header));
        allWithdrawalsAmounts.forEach(amount -> assertThat(outputStream.toString()).contains(amount));
        assertThat(outputStream.toString()).contains(sumsOfTransactionsForPrinting.getSumOfWithdrawals().toString());

        System.setOut(originalOut);
    }
}
