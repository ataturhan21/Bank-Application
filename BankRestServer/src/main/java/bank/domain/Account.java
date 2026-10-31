package bank.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;

@Entity
public class Account {

    @Id
    private long accountNumber;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "accountNumber")
    private Collection<AccountEntry> entryList = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customerId")
    private Customer customer;

    public Account() {
        // Default constructor for JPA
    }

    public Account(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return entryList.stream().mapToDouble(AccountEntry::getAmount).sum();
    }

    public void deposit(double amount) {
        validateAmount(amount);
        addEntry(new AccountEntry(new Date(), amount, "deposit", "", ""));
    }

    public void withdraw(double amount) {
        validateAmount(amount);
        addEntry(new AccountEntry(new Date(), -amount, "withdraw", "", ""));
    }

    public void addEntry(AccountEntry entry) {
        entryList.add(entry);
    }

    public void transferFunds(Account toAccount, double amount, String description) {
        validateAmount(amount);
        if (toAccount == null || customer == null || toAccount.getCustomer() == null) {
            throw new IllegalArgumentException("Both accounts must have a customer before transferring funds");
        }
        Date transferredAt = new Date();
        AccountEntry debit = new AccountEntry(transferredAt, -amount, description,
                String.valueOf(toAccount.getAccountNumber()), toAccount.getCustomer().getName());
        AccountEntry credit = new AccountEntry(transferredAt, amount, description,
                String.valueOf(accountNumber), customer.getName());
        addEntry(debit);
        toAccount.addEntry(credit);
    }

    private static void validateAmount(double amount) {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Amount must be finite and non-negative");
        }
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Collection<AccountEntry> getEntryList() {
        return new ArrayList<>(entryList);
    }

    @Override
    public String toString() {
        return String.format("Account{accountNumber=%d, balance=%.2f, customer=%s}", accountNumber, getBalance(), customer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return accountNumber == account.accountNumber;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(accountNumber);
    }
}

