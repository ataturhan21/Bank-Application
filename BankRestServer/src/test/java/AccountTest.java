import bank.domain.Account;
import bank.domain.AccountEntry;
import bank.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {
    private Account toAccount;
    private Account fromAccount;

    @BeforeEach
    public void setup() {
        Customer customer1 = new Customer("Alice Johnson");
        toAccount = new Account(200);
        toAccount.setCustomer(customer1);

        Customer customer2 = new Customer("Bob Smith");
        fromAccount = new Account(100);
        fromAccount.setCustomer(customer2);
    }

    @Test
    public void testCreateAccount() {
        assertThat(toAccount.getBalance(), closeTo(0.0, 0.01));
    }

    @Test
    public void testDepositZero() {
        toAccount.deposit(0.0);
        assertThat(toAccount.getBalance(), closeTo(0.0, 0.01));
    }

    @Test
    public void testDeposit() {
        toAccount.deposit(200.0);
        assertThat(toAccount.getBalance(), closeTo(200.0, 0.01));
    }

    @Test
    public void testWithdrawZero() {
        toAccount.withdraw(0.0);
        assertThat(toAccount.getBalance(), closeTo(0.0, 0.01));
    }

    @Test
    public void testWithdraw() {
        toAccount.withdraw(50.0);
        assertThat(toAccount.getBalance(), closeTo(-50.0, 0.01));
    }

    @Test
    public void testTransferFundsZero() {
        fromAccount.transferFunds(toAccount, 0.0, "for groceries");
        assertThat(fromAccount.getBalance(), closeTo(0.0, 0.01));
        assertThat(toAccount.getBalance(), closeTo(0.0, 0.01));
    }

    @Test
    public void testTransferFunds() {
        fromAccount.transferFunds(toAccount, 150.0, "for rent");
        assertThat(fromAccount.getBalance(), closeTo(-150.0, 0.01));
        assertThat(toAccount.getBalance(), closeTo(150.0, 0.01));
        AccountEntry debit = fromAccount.getEntryList().iterator().next();
        AccountEntry credit = toAccount.getEntryList().iterator().next();
        assertEquals("200", debit.getFromAccountNumber());
        assertEquals("Alice Johnson", debit.getFromPersonName());
        assertEquals("100", credit.getFromAccountNumber());
        assertEquals("Bob Smith", credit.getFromPersonName());
    }

    @Test
    public void testInvalidAmountsLeaveBothAccountsUnchanged() {
        fromAccount.deposit(200.0);
        double[] invalidAmounts = {-1.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for (double amount : invalidAmounts) {
            assertThrows(IllegalArgumentException.class, () -> fromAccount.deposit(amount));
            assertThrows(IllegalArgumentException.class, () -> fromAccount.withdraw(amount));
            assertThrows(IllegalArgumentException.class,
                    () -> fromAccount.transferFunds(toAccount, amount, "invalid"));
        }
        assertEquals(200.0, fromAccount.getBalance());
        assertEquals(0.0, toAccount.getBalance());
        assertEquals(1, fromAccount.getEntryList().size());
        assertEquals(0, toAccount.getEntryList().size());
    }

    @Test
    public void testIncompleteTransferLeavesSourceUnchanged() {
        assertThrows(IllegalArgumentException.class,
                () -> fromAccount.transferFunds(null, 25.0, "invalid"));
        toAccount.setCustomer(null);
        assertThrows(IllegalArgumentException.class,
                () -> fromAccount.transferFunds(toAccount, 25.0, "invalid"));
        assertEquals(0, fromAccount.getEntryList().size());
        assertEquals(0, toAccount.getEntryList().size());
    }
}

