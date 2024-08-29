package Banking;

import java.util.Scanner;
import java.util.HashMap;
import java.util.LinkedList;


public class BankAccount {

	private double balance;
	private LinkedList<String> transactionHistory;

	// Default constructor
	public BankAccount() {
		this.balance = 0.0;
		this.transactionHistory = new LinkedList<>();
	}

	// Contructor with parameter to set initial balance
	public BankAccount(double initialBalance) {
		this.balance = initialBalance;
		this.transactionHistory = new LinkedList<>();
		this.transactionHistory.add("Account created with initial balance: " + initialBalance + "€");
	}

	// Method to deposit some money into the account
	public void deposit(double amount) {
		if (amount > 0) {
			balance += amount;
			System.out.println("Deposited: " + amount + "€");
		} else {
			System.out.println("Deposit amount must be positive (>0).");
		}
	}

	// Method to Withdraw amount from the account
	public void withdraw(double amount) {
		if (amount > 0 && amount <= balance) {
			balance -= amount;
			System.out.println("Withdrew: " + amount + "€");
		} else {
			System.out.println("Withdrawal amount must be posivite (>0)");
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
			System.out.println("Transfered: " + amount + "€");
		} else if (amount > balance) {
			System.out.println("Insufficient funds for transfer.");
		} else {
			System.out.println("Transfer amount must be positive (>0)");
		}
	}

	// Method to print transaction history
	public void printTransactionHistory() {
		System.out.println("Transaction history: ");
		for (String transaction : transactionHistory) {
			System.out.println(transaction);
		}
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// Create a HashMap to store multiple bank accounts with unique IDs
		HashMap<String, BankAccount> accounts = new HashMap<>();

		// Adding two sample accounts
		accounts.put("Account1", new BankAccount());
		accounts.put("Account2", new BankAccount(100.00));

		boolean running = true;
		while (running) {
			System.out.println("\nBank Account:");
			System.out.println("1. Deposit");
			System.out.println("2. Withdraw");
			System.out.println("3. Transfer");
			System.out.println("4. Print Balance");
			System.out.println("5. Print Transaction History");
			System.out.println("6. Exit");
			System.out.println("Choose an option:");

			int choice = scanner.nextInt();

			switch (choice) {
				case 1:
					System.out.println("Enter account ID for deposit: ");
					String depositAccountId = scanner.next();
					System.out.println("Enter deposit amount: ");
					double depositAmount = scanner.nextDouble();
					if (accounts.containsKey(depositAccountId)) {
						accounts.get(depositAccountId).deposit(depositAmount);
					} else {
						System.out.println("Account ID not found.");
					}
					break;
				case 2:
					System.out.println("Enter account ID for withdrawal: ");
					String withdrawAccountId = scanner.next();
					System.out.println("Enter withdrawal amount: ");
					double withdrawAmount = scanner.nextDouble();
					if (accounts.containsKey(withdrawAccountId)) {
						accounts.get(withdrawAccountId).withdraw(withdrawAmount);
					} else {
						System.out.println("Account ID not found.");
					}
					break;
				case 3:
					System.out.println("Enter account ID to transfer from: ");
					String fromAccountId = scanner.next();
					System.out.println("Enter account ID to transfer to: ");
					String toAccountId = scanner.next();
					System.out.println("Enter transfer amount: ");
					double transferAmount = scanner.nextDouble();
					if (accounts.containsKey(fromAccountId) && accounts.containsKey(toAccountId)) {
						accounts.get(fromAccountId).transfer(accounts.get(toAccountId), transferAmount);
					} else {
						System.out.println("One or both account IDs not found.");
					}
					break;
				case 4:
					System.out.println("Enter account ID to print balance: ");
					String balanceAccountId = scanner.next();
					if (accounts.containsKey(balanceAccountId)) {
						accounts.get(balanceAccountId).printBalance();
					} else {
						System.out.println("Account ID not found.");
					}
					break;
				case 5:
					System.out.println("Enter account ID to print transaction history: ");
					String historyAccountId = scanner.next();
					if (accounts.containsKey(historyAccountId)) {
						accounts.get(historyAccountId).printTransactionHistory();
					} else {
						System.out.println("Account ID not found.");
					}
					break;
				case 6:
					running = false;
					System.out.println("Exiting...");
					break;
				default:
					System.out.println("Invalid option. Please try again.");
					break;
			}
		}
		scanner.close();
	}
}

