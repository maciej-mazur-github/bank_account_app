import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import societegeneralekata.io.Printer;
import societegeneralekata.model.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AccountTest {

    private final Printer printer = new Printer();

    private final Account account = new Account(TestUtils.generateGeneralPreTransactions(), printer);

    @Test
    void shouldHaveBalanceEqualToExpectedOne() {
        // given, when
        List<Transaction> transactionsDeepCopy = account.getTransactionsDeepCopy();
        BigDecimal calculatedBalance = TestUtils.calculateBalance(transactionsDeepCopy);

        // then
        assertThat(account.getBalance()).isEqualTo(calculatedBalance);
    }

    @Test
    void shouldIncreaseBalanceBy2000AfterMakingDepositOf2000() {
        // given
        BigDecimal balanceBeforeDeposit = account.getBalance();
        int numberOfRegisteredTransactionsBeforeDeposit = account.getTransactionsDeepCopy().size();

        // when
        account.makeDeposit(new BigDecimal("2000"), "Special bonus");

        // then
        List<Transaction> transactionsAfterDeposit = account.getTransactionsDeepCopy();

        assertThat(account.getBalance()).isEqualTo(balanceBeforeDeposit.add(new BigDecimal("2000")));
        assertThat(transactionsAfterDeposit.size()).isEqualTo(numberOfRegisteredTransactionsBeforeDeposit + 1);
        assertThat(transactionsAfterDeposit.get(transactionsAfterDeposit.size() - 1).getDescription()).isEqualTo("Special bonus");
    }

    @ParameterizedTest(name = "Deposit of {arguments}:")
    @MethodSource("provideSeriesOfDeposits")
    void shouldIncreaseBalanceProperlyAfterMakingVariousDeposits(BigDecimal amount, String description) {
        // given
        BigDecimal balanceBeforeDeposit = account.getBalance();
        int numberOfRegisteredTransactionsBeforeDeposit = account.getTransactionsDeepCopy().size();

        // when
        account.makeDeposit(amount, description);

        // then
        List<Transaction> transactionsAfterDeposit = account.getTransactionsDeepCopy();

        assertThat(account.getBalance()).isEqualTo(balanceBeforeDeposit.add(amount));
        assertThat(transactionsAfterDeposit.size()).isEqualTo(numberOfRegisteredTransactionsBeforeDeposit + 1);
        assertThat(transactionsAfterDeposit.get(transactionsAfterDeposit.size() - 1).getDescription()).isEqualTo(description);
    }

    @ParameterizedTest(name = "Withdrawal of {arguments}:")
    @MethodSource("provideSeriesOfWithdrawals")
    void shouldDecreaseBalanceProperlyAfterMakingVariousWithdrawals(BigDecimal amount, String description) {
        // given
        BigDecimal balanceBeforeWithdrawal = account.getBalance();
        int numberOfRegisteredTransactionsBeforeWithdrawal = account.getTransactionsDeepCopy().size();

        // when
        account.makeWithdrawal(amount, description);

        // then
        List<Transaction> transactionsAfterDeposit = account.getTransactionsDeepCopy();

        assertThat(account.getBalance()).isEqualTo(balanceBeforeWithdrawal.subtract(amount));
        assertThat(transactionsAfterDeposit.size()).isEqualTo(numberOfRegisteredTransactionsBeforeWithdrawal + 1);
        assertThat(transactionsAfterDeposit.get(transactionsAfterDeposit.size() - 1).getDescription()).isEqualTo(description);
    }

    @Test
    void shouldNotPerformWithdrawalWhenTransactionAmountExceedsBalance() {
        // given
        BigDecimal initialBalance = account.getBalance();
        BigDecimal withdrawalAmount = initialBalance.add(new BigDecimal("1000"));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // when
        account.makeWithdrawal(withdrawalAmount, "Too big withdrawal");

        // then
        assertThat(account.getBalance()).isEqualTo(initialBalance);
        assertThat(outputStream.toString()).isEqualTo(String.format(
                "There are insufficient funds in your account to proceed with withdrawing requested %s EUR.%n",
                withdrawalAmount.setScale(2, RoundingMode.HALF_DOWN)));

        System.setOut(originalOut);
    }

    @Test
    void shouldOnlyRegisterTransactionsThatAreValid() {
        // given
        List<PreTransaction> validAndInvalidPreTransactions = TestUtils.generateValidAndInvalidPreTransactions();
        int initialNumberOfTransactionsInAccount = account.getTransactionsDeepCopy().size();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // when
        TestUtils.performPreTransactions(validAndInvalidPreTransactions, account);

        // then
        assertThat(account.getTransactionsDeepCopy().size() - initialNumberOfTransactionsInAccount).isEqualTo( 3);
        assertThat(outputStream.toString()).contains("Transaction amount must be greater than zero.");
        assertThat(outputStream.toString()).contains(
                "There are insufficient funds in your account to proceed with withdrawing requested 800.00 EUR.");
        assertThat(outputStream.toString()).contains("You have no funds in your account, therefore requested withdrawal was not proceeded.");

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

    @Test
    void shouldMakeBalanceZeroWhenFullFundsWithdrawalIsRequested() {
        // given, when
        account.withdrawAllFunds();

        // then
        assertThat(account.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    private static Stream<Arguments> provideSeriesOfWithdrawals() {
        return Stream.of(
                Arguments.of(new BigDecimal("1000"), "Expense 1"),
                Arguments.of(new BigDecimal("7000"), "Expense 2"),
                Arguments.of(new BigDecimal("500"), "Expense 3"),
                Arguments.of(new BigDecimal("400"), "Expense 4"),
                Arguments.of(new BigDecimal("1100"), "Expense 5")
        );
    }

    private static Stream<Arguments> provideSeriesOfDeposits() {
        return Stream.of(
                Arguments.of(new BigDecimal("1000"), "Bonus 1"),
                Arguments.of(new BigDecimal("7000"), "Bonus 2"),
                Arguments.of(new BigDecimal("500"), "Bonus 3"),
                Arguments.of(new BigDecimal("400"), "Bonus 4"),
                Arguments.of(new BigDecimal("1100"), "Bonus 5")
        );
    }
}