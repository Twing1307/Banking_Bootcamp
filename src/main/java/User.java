import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {
    private String username;
    private String password;
    private HashMap<String, BankAccount> accounts;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.accounts = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void addAccount(String accountId, BankAccount account) {
        accounts.put(accountId, account);
    }

    public BankAccount getAccount(String accountId) {
        return accounts.get(accountId);
    }

    public HashMap<String, BankAccount> getAccounts() {
        return accounts;
    }
}
