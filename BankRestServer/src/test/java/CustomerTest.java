import bank.domain.Customer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CustomerTest {

    @Test
    public void equalsHandlesNullNames() {
        Customer unnamed = new Customer();
        Customer named = new Customer("Alice Johnson");

        assertEquals(unnamed, new Customer());
        assertNotEquals(unnamed, named);
        assertNotEquals(named, unnamed);
    }
}
