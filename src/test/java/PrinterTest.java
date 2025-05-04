import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import kata.io.Printer;
import kata.model.Account;
import kata.model.SumsOfTransactionsForPrinting;
import kata.model.Transaction;
import kata.model.TransactionType;
import kata.utils.Calculator;
import kata.utils.Filter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;

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

    @Test
    void shouldHaveProperNonNullAndNullArgumentsWhenPrintingAllTypesOfTransactionsWithoutTimeRange() {
        // given
        Printer printerMock = Mockito.mock(Printer.class);
        Account testAccount = new Account(printerMock);

        ArgumentCaptor<SumsOfTransactionsForPrinting> sumsOfTransactionsForPrintingArgumentCaptor = ArgumentCaptor.forClass(
                SumsOfTransactionsForPrinting.class);
        ArgumentCaptor<LocalDateTime> startDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        // when
        testAccount.printAllTransactionHistory();
        testAccount.printAllDeposits();
        testAccount.printAllWithdrawals();

        // then
        Mockito.verify(printerMock, Mockito.times(3)).printTransactionHistory(
                anyList(),
                sumsOfTransactionsForPrintingArgumentCaptor.capture(),
                startDateCaptor.capture(),
                endDateCaptor.capture());

        BigDecimal allTransactionsBalance = sumsOfTransactionsForPrintingArgumentCaptor.getAllValues().get(0).getBalance();
        BigDecimal allTransactionsDeposits = sumsOfTransactionsForPrintingArgumentCaptor.getAllValues().get(0).getSumOfDeposits();
        BigDecimal allTransactionsWithdrawals = sumsOfTransactionsForPrintingArgumentCaptor.getAllValues().get(0).getSumOfWithdrawals();
        LocalDateTime allTransactionsStartDate = startDateCaptor.getAllValues().get(0);
        LocalDateTime allTransactionsEnDate = endDateCaptor.getAllValues().get(0);

        assertThat(allTransactionsBalance).isNotNull();
        assertThat(allTransactionsDeposits).isNotNull();
        assertThat(allTransactionsWithdrawals).isNotNull();
        assertThat(allTransactionsStartDate).isNull();
        assertThat(allTransactionsEnDate).isNull();

        BigDecimal allDepositsBalance = sumsOfTransactionsForPrintingArgumentCaptor.getAllValues().get(1).getBalance();
        BigDecimal allDepositsDeposits = sumsOfTransactionsForPrintingArgumentCaptor.getAllValues().get(1).getSumOfDeposits();
        BigDecimal allDepositsWithdrawals = sumsOfTransactionsForPrintingArgumentCaptor.getAllValues().get(1).getSumOfWithdrawals();
        LocalDateTime allDepositsStartDate = startDateCaptor.getAllValues().get(1);
        LocalDateTime allDepositsEnDate = endDateCaptor.getAllValues().get(1);

        assertThat(allDepositsBalance).isNull();
        assertThat(allDepositsDeposits).isNotNull();
        assertThat(allDepositsWithdrawals).isNull();
        assertThat(allDepositsStartDate).isNull();
        assertThat(allDepositsEnDate).isNull();

        BigDecimal allWithdrawalsBalance = sumsOfTransactionsForPrintingArgumentCaptor.getAllValues().get(2).getBalance();
        BigDecimal allWithdrawalsDeposits = sumsOfTransactionsForPrintingArgumentCaptor.getAllValues().get(2).getSumOfDeposits();
        BigDecimal allWithdrawalsWithdrawals = sumsOfTransactionsForPrintingArgumentCaptor.getAllValues().get(2).getSumOfWithdrawals();
        LocalDateTime allWithdrawalsStartDate = startDateCaptor.getAllValues().get(2);
        LocalDateTime allWithdrawalsEnDate = endDateCaptor.getAllValues().get(2);

        assertThat(allWithdrawalsBalance).isNull();
        assertThat(allWithdrawalsDeposits).isNull();
        assertThat(allWithdrawalsWithdrawals).isNotNull();
        assertThat(allWithdrawalsStartDate).isNull();
        assertThat(allWithdrawalsEnDate).isNull();
    }
}
