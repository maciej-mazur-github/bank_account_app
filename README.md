# Simple Bank Account App

Simple project of a very limited functionality (exactly as per provided requirements) for simulating basic bank account transaction operations and for printing the transactions history.

## Table Of Content
<ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#some-information-on-implementation-details">Some Information On Implementation Details</a>
      <ul>
        <li><a href="#account-class-constructor-versions">Account class constructor versions</a></li>
        <li><a href="#account-class-public-methods">Account class public methods</a></li>
        <li><a href="#printer-class-functionality-and-implementation">Printer class functionality and implementation</a></li>
        <li><a href="#unit-tests">Unit tests</a></li>
      </ul>
    </li>
    
  </ol>

## About The Project

The program allows to perform below listed basic account operations:

- make a deposit
- make a partial withdrawal
- make a full withdrawal

Each transaction contains date and time of operation, 

The program offers several console printing options to show the transactions history:
- print all transactions 
- print all deposits only
- print all withdrawals only (including the full ones)
- print transactions filtered by time range and/or by transaction type (DEPOSIT, WITHDRAWAL or FULL_WITHDRAWAL)
- print transactions filtered by time range only (including all possible transaction types)
- reset a password with the resetting link sent via email

## Built With

| Technology / Language     | Description |
| -----------: | ----------- |
|[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://docs.oracle.com/en/java/javase/21/docs/api/index.html)|Oracle JDK 21.0.3|
|[![Lombok](https://img.shields.io/badge/Project_Lombok-ED8B00?style=for-the-badge&logoColor=white)](https://projectlombok.org/)|To reduce a code bloat|
|[![](https://img.shields.io/badge/junit-%23E33332?logo=junit5&logoColor=white)](https://junit.org/junit5/) | For unit testing|
|[![AssertJ](https://img.shields.io/badge/AssertJ-ED8B00?style=for-the-badge&logoColor=white)](https://assertj.github.io/doc/)| For unit testing assertions |
|[![Mockito](https://img.shields.io/badge/Mockito-51803a?style=for-the-badge&logoColor=white)](https://site.mockito.org/)| For unit testing mocks |
|[![ASCII Table](https://img.shields.io/badge/AsciiTable-51803a?style=for-the-badge&logoColor=white)](http://www.vandermeer.de/projects/skb/java/asciitable/features.html)| External library to build dynamic ASCII tables when printing transaction history in various ways; http://www.vandermeer.de/projects/skb/java/asciitable/features.html https://github.com/vdmeer/asciitable |


## Some Information On Implementation Details
### Account class constructor versions

There are 2 constructors. Both inject Printer class dependency as it made unit testing easier.

- Account(Printer printer) - builds an Account class object with empty transactions list (for simulating the fresh new start of the new account)
- Account(List<PreTransaction> preTransactions, Printer printer) - builds an account with the pre-existing transactions. You can create your own list of PreTransaction objects as an input or you can use **RandomTransactionsGenerator.generateRandomPreTransactions()** static method to generate 50 such objects randomly. The constructor then will iterate over all this list elements and will attempt to perform each of the preTransactions based on each one's transaction type. Whenever the operation is possible, it will be registered in Account's class private list of transactions. The chronology of operations will remain intact. Whenever the operation is **not** possible, the respective information will be printed to the console and operation will not be registered in the Account's class private list of operations.

### Account class public methods
- makeDeposit(BigDecimal amount, String description) - transaction date will be set as LocalDateTime.now(), so there is no possibility for the user to re-write transaction history. Attempt to deposit zero amount or a negative one will result in disregarding the transaction request and in printing the respective message to the console
- makeWithdrawal(BigDecimal amount, String description) - same as above in terms of setting the date. Withdrawal amount cannot be zero. If the amount is provided as a negative one, this amount will be automatically set to positive one. The assumption is that the client cannot have a debit on the account, therefore it is not possible to withdraw the amount exceeding the current account balance. 
- withdrawAllFunds() - same as above in terms of setting the date. You cannot perform full withdrawal when the account balance is zero.

Whenever the requested transaction turns out to be valid, the respective message will be printed to the console.

Printing methods:
- printAllTransactionHistory() - prints all the transaction history, with no filters
- printAllDeposits() - prints deposits only, with no time range filter
- printAllWithdrawals() - prints withdrawals (**including full withdrawals**) only, with no time range filter
- printTransactionsInTimeRange(LocalDate startTime, LocalDate endTime) - allows to print transactions filtered by time range
- printTransactionsByTimeRangeAndType(LocalDate start, LocalDate end, TransactionType type) - allows to print transactions filtered by time range and also by transaction type

### Printer class functionality and implementation
Printer class is used to print both simple messages and dynamically built ASCII tables for various ways of presenting the transaction history.<br><br>
External library has been used to dynamically build ASCII tables:
<br> 
- https://github.com/vdmeer/asciitable 
<br> 
- http://www.vandermeer.de/projects/skb/java/asciitable/features.html 


<br>
Depending on the chosen printing option the table will have various number of columns 
- 3 columns when printAllDeposits(), printAllWithdrawals() or printTransactionsByTimeRangeAndType() is used
- 5 when printAllTransactionHistory() or printTransactionsInTimeRange is used.

<br>
printTransactionHistory() will always calculate the desired number of columns based on whether the respective method arguments are **null** or not.
<br> <br>
The first table row contains either the information about the time range of printed transactions or the information about no transactions currently registered for a given time range (like when no transactions suit given time range and/or type).
<br><br>
The second table row contains actual column names. E.g. when printAllDeposits() is chosen, the column names will be DATE, DESCRIPTION, DEPOSIT. WITHDRAWAL and BALANCE will not be printed. When printAllTransactionHistory() is used, all columns DATE, DESCRIPTION, DEPOSIT, WITHDRAWAL and BALANCE will be printed.
<br><br>
Then the table is fulfilled with proper filtered transaction details, reflecting the transaction type, i.e. when the given transaction is DEPOSIT, its amount will be printed in DEPOSIT column only, the WITHDRAWAL cell will be left empty.
<br><br>
Each table has its footer row with respective total transaction sums, printed in the respective columns, i.e. the sum of deposits will be printed in the DEPOSIT column, sum of withdrawals will be in WITHDRAWAL column and account balance will be in BALANCE column (providing these columns are supposed to be printed in the first place).

### Unit tests
JUnit5, AssertJ and Mockito libraries have been used to perform different unit test case scenarios.
<br><br>
- **AccountTest** checks whether the account balance and private transaction list are modified properly in case of multiple transaction scenarios, especially to make sure that invalid operations (like withdrawing the amount exceeding the current balance) are not registered in the account's private transaction list.
<br><br>
**TestUtils.generateGeneralPreTransactions()** method has been used to generate a pre-determined list of preTransactions. This list is passed to Account constructor as an input argument. 
<br>**TestUtils.performPreTransactions()** is used to perform all the preTransactions. 
<br>**TestUtils.generateValidAndInvalidPreTransactions()** is used to generate various transactions with the intention for several of them to be rejected.
<br>
**ByteArrayOutputStream** has been used to check, whether the console messages contain the expected error messages for invalid operations.

- **PrinterTest** checks whether the expected column names are printed, whether the expected transaction amounts are printed and whether the expected total sums are printed. It also makes sure that the undesired column names or transactions sums are **not** printed. AssertJ assertions are used during iterating over various collections.
<br>
**Mockito mocks and ArgumentCaptor** are used here either to verify, whether the proper lists of arguments are used when calling **Printer.printTransactionHistory()**, as these arguments greatly influence which transaction details are printed and which are not and also the form of ASCII table.

- **FilterTest** tests various filtering scenarios.