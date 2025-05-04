package kata.io;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.asciithemes.u8.U8_Grids;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import kata.model.SumsOfTransactionsForPrinting;
import kata.model.Transaction;
import kata.model.TransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Printer {
    public void printLine(String text) {
        System.out.println(text);
    }

    public void printTransactionHistory(List<Transaction> transactions,
                                        SumsOfTransactionsForPrinting sumsOfTransactionsForPrinting,
                                        LocalDateTime startDateTime,
                                        LocalDateTime endDateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        AsciiTable table = new AsciiTable();
        table.addRule();
        buildTop2TableRows(
                table,
                sumsOfTransactionsForPrinting.getBalance(),
                startDateTime,
                endDateTime,
                transactions,
                formatter,
                sumsOfTransactionsForPrinting.getSumOfDeposits(),
                sumsOfTransactionsForPrinting.getSumOfWithdrawals());

        buildTransactionRows(
                transactions,
                sumsOfTransactionsForPrinting.getBalance(),
                sumsOfTransactionsForPrinting.getSumOfDeposits(),
                sumsOfTransactionsForPrinting.getSumOfWithdrawals(),
                formatter,
                table);

        buildTableFooter(
                sumsOfTransactionsForPrinting.getBalance(),
                sumsOfTransactionsForPrinting.getSumOfDeposits(),
                sumsOfTransactionsForPrinting.getSumOfWithdrawals(),
                table);
        table.addRule();

        // set of de.vandermeer.asciitable.AsciiTable configuration rules and printing the table
        table.getContext().setGrid(U8_Grids.borderDouble());
        table.getRenderer().setCWC(new CWC_LongestLine());
        table.setTextAlignment(TextAlignment.CENTER);
        table.setPaddingLeftRight(3);
        printLine(table.render());
    }

    private void buildTableFooter(BigDecimal balance, BigDecimal sumOfDeposits, BigDecimal sumOfWithdrawals, AsciiTable table) {
        List<String> footerRow = new ArrayList<>();
        footerRow.add(null);
        footerRow.add("Totals:");
        if (sumOfDeposits != null) {
            footerRow.add("\u20ac " + sumOfDeposits.setScale(2, RoundingMode.HALF_DOWN));
        }
        if (sumOfWithdrawals != null) {
            footerRow.add("\u20ac " + sumOfWithdrawals.setScale(2, RoundingMode.HALF_DOWN));
        }
        if (balance != null) {
            footerRow.add("\u20ac " + balance.setScale(2, RoundingMode.HALF_DOWN));
        }
        table.addRow(footerRow.toArray());
    }

    private static void buildTransactionRows(List<Transaction> transactions,
                                             BigDecimal balance,
                                             BigDecimal sumOfDeposits,
                                             BigDecimal sumOfWithdrawals,
                                             DateTimeFormatter formatter,
                                             AsciiTable table) {
        if (transactions.isEmpty()) {
            return;
        }

        transactions.forEach(t -> {
            List<String> transactionRow = new ArrayList<>();
            transactionRow.add(t.getDate().format(formatter));
            transactionRow.add(t.getDescription());
            if (sumOfDeposits != null) {
                String depositCell = t.getType() == TransactionType.DEPOSIT ?
                        "\u20ac " + t.getAmount().setScale(2, RoundingMode.HALF_DOWN)
                        :
                        "";
                transactionRow.add(depositCell);
            }
            if (sumOfWithdrawals != null) {
                String withdrawalCell = t.getType() == TransactionType.WITHDRAWAL || t.getType() == TransactionType.FULL_WITHDRAWAL ?
                        "\u20ac " + t.getAmount().setScale(2, RoundingMode.HALF_DOWN)
                        :
                        "";
                transactionRow.add(withdrawalCell);
            }
            if (balance != null) {
                String balanceCell = "\u20ac " + t.getBalance().setScale(2, RoundingMode.HALF_DOWN);
                transactionRow.add(balanceCell);
            }
            table.addRow(transactionRow.toArray());
        });

        table.addRule();
    }

    private void buildTop2TableRows(AsciiTable table,
                                    BigDecimal balance,
                                    LocalDateTime startDateTime,
                                    LocalDateTime endDateTime,
                                    List<Transaction> transactions,
                                    DateTimeFormatter formatter,
                                    BigDecimal sumOfDeposits,
                                    BigDecimal sumOfWithdrawals) {

        buildFirstTableRow(table, balance, startDateTime, endDateTime, transactions, formatter);
        table.addRule();
        buildSecondTableRow(table, balance, sumOfDeposits, sumOfWithdrawals);
        table.addRule();
    }

    private void buildSecondTableRow(AsciiTable table, BigDecimal balance, BigDecimal sumOfDeposits, BigDecimal sumOfWithdrawals) {
        List<String> secondRowList = new ArrayList<>();
        secondRowList.add("DATE");
        secondRowList.add("DESCRIPTION");
        if (sumOfDeposits != null) {
            secondRowList.add("DEPOSIT");
        }
        if (sumOfWithdrawals != null) {
            secondRowList.add("WITHDRAWAL");
        }
        if (balance != null) {
            secondRowList.add("BALANCE");
        }
        table.addRow(secondRowList.toArray());
    }

    private void buildFirstTableRow(AsciiTable table,
                                    BigDecimal balance,
                                    LocalDateTime startDateTime,
                                    LocalDateTime endDateTime,
                                    List<Transaction> transactions,
                                    DateTimeFormatter formatter) {

        String title;
        List<String> firstRowList = new ArrayList<>();
        int numberOfColumns = balance == null ? 3 : 5;

        for (int i = 0; i < numberOfColumns - 1; i++) {
            firstRowList.add(null);
        }

        if (!transactions.isEmpty() || (startDateTime != null && endDateTime != null)) {
            LocalDateTime start = startDateTime == null ? transactions.get(0).getDate() : startDateTime;
            LocalDateTime end = endDateTime == null ? transactions.get(transactions.size() - 1).getDate() : endDateTime;

            title = String.format("Transactions %s - %s", start.format(formatter), end.format(formatter));
        } else {
            title = "No transactions registered for a specified transaction type";
        }

        firstRowList.add(title);
        table.addRow(firstRowList.toArray());
    }
}
