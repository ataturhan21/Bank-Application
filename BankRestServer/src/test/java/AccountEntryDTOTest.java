import bank.dto.AccountEntryDTO;

import java.util.Date;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AccountEntryDTOTest {

    @Test
    public void missingDateStaysNull() {
        AccountEntryDTO entry = new AccountEntryDTO();
        assertNull(entry.getDate());
        entry.setDate(null);
        assertNull(entry.getDate());
    }

    @Test
    public void dateIsCopiedDefensively() {
        Date date = new Date(1_000L);
        AccountEntryDTO entry = new AccountEntryDTO();
        entry.setDate(date);
        date.setTime(2_000L);
        assertEquals(1_000L, entry.getDate().getTime());
        assertNotSame(entry.getDate(), entry.getDate());
    }
}
