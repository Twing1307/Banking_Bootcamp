import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class BankAccount {

    private double balance;
    private LinkedList<String> transactionHistory;

    // Default constructor
    public BankAccount() {
        this.balance = 0.0;
        this.transactionHistory = new LinkedList<>();
        this.transactionHistory.add("Account created with initial balance: 0.0€");
    }

    // Constructor with parameter to set initial balance
    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
        this.transactionHistory = new LinkedList<>();
        this.transactionHistory.add("Account created with initial balance: " + initialBalance + "€");
    }

    // Method to deposit some money into the account
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactionHistory.add("Deposited: " + amount + "€ | New balance: " + balance + "€");
            System.out.println("Deposited: " + amount + "€");
        } else {
            System.out.println("Deposit amount must be positive (>0).");
        }
    }

    // Method to withdraw amount from the account
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactionHistory.add("Withdrew: " + amount + "€ | New balance: " + balance + "€");
            System.out.println("Withdrew: " + amount + "€");
        } else {
            System.out.println("Withdrawal amount must be positive and less than or equal to balance.");
        }
    }

    // Method to print balance
    public void printBalance() {
        System.out.println("Current balance: " + balance + "€");
    }

    // Method to transfer balance from one bank account to another
    public void transfer(BankAccount otherAccount, double amount) {
        if (amount > 0 && amount <= balance) {
            this.withdraw(amount);
            otherAccount.deposit(amount);
            transactionHistory.add("Transferred: " + amount + "€ to another account | New balance: " + balance + "€");
            System.out.println("Transferred: " + amount + "€");
        } else if (amount > balance) {
            System.out.println("Insufficient funds for transfer.");
        } else {
            System.out.println("Transfer amount must be positive (>0).");
        }
    }

    // Method to print transaction history
    public void printTransactionHistory() {
        System.out.println("Transaction history: ");
        for (String transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }

    // Method to get the current balance
    public double getBalance() {
        return balance;
    }

    // Method to get the transaction history
    public LinkedList<String> getTransactionHistory() {
        return transactionHistory;
    }

    // Method to generate a report of all accounts
    public static void generateReport(HashMap<String, BankAccount> accounts) throws IOException {
        FileWriter writer = new FileWriter("bank_accounts_report.txt");
        writer.write("AccountID, Balance, Transactions\n");
        for (String accountId : accounts.keySet()) {
            BankAccount account = accounts.get(accountId);
            writer.write(accountId + ", " + account.getBalance() + "€\n");
            for (String transaction : account.getTransactionHistory()) {
                writer.write("\t" + transaction + "\n");
            }
            writer.write("\n");
        }
        writer.close();
        System.out.println("Report generated: bank_accounts_report.txt");
    }
}
