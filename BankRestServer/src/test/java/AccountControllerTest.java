import bank.controller.AccountController;
import bank.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

public class AccountControllerTest {
    private AccountController controller;
    private AccountService service;

    @BeforeEach
    public void setup() {
        controller = new AccountController();
        service = mock(AccountService.class);
        ReflectionTestUtils.setField(controller, "accountService", service);
    }

    @Test
    public void invalidAmountsReturnBadRequestWithoutMutatingAccounts() {
        for (double amount : new double[]{-1.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertEquals(HttpStatus.BAD_REQUEST,
                    controller.accountOperation(100, amount, "deposit", null, null).getStatusCode());
        }
        verifyNoInteractions(service);
    }

    @Test
    public void transferRequiresDestinationAccount() {
        assertEquals(HttpStatus.BAD_REQUEST,
                controller.accountOperation(100, 25, "transferFunds", null, "rent").getStatusCode());
        verifyNoInteractions(service);
    }

    @Test
    public void unknownOperationReturnsBadRequest() {
        assertEquals(HttpStatus.BAD_REQUEST,
                controller.accountOperation(100, 25, "unknown", null, null).getStatusCode());
        verifyNoInteractions(service);
    }
}
