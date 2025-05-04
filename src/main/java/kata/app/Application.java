package kata.app;

import kata.io.Printer;
import kata.model.Account;
import kata.utils.RandomTransactionsGenerator;

public class Application {
    public static void main(String[] args) {
        Printer printer = new Printer();
        Account account = new Account(RandomTransactionsGenerator.generateRandomPreTransactions(), printer);
//        Account account = new Account(printer);
//        registerTestTransactions(account);

        account.printAllTransactionHistory();
//        account.printTransactionsInTimeRange(LocalDate.of(2025, 1, 22), LocalDate.of(2025, 2, 8));
//        account.printAllDeposits();
//        account.printAllWithdrawals();
//        account.printTransactionsInTimeRange(
//                LocalDate.of(2025, 1, 15),
//                LocalDate.of(2025, 2, 15));

//        account.printTransactionsByTimeRangeAndType(
//                LocalDate.of(2025, 1, 15),
//                LocalDate.of(2025, 2, 15),
//                TransactionType.DEPOSIT);
    }

}
