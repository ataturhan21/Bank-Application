import bank.controller.AccountController;
import bank.dto.AccountDTO;
import bank.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Test
    public void unknownAccountReturnsNotFound() {
        when(service.getAccount(404)).thenReturn(null);
        assertEquals(HttpStatus.NOT_FOUND, controller.getAccount(404).getStatusCode());
        assertNull(controller.getAccount(404).getBody());
    }

    @Test
    public void operationOnUnknownAccountReturnsNotFoundWithoutMutating() {
        when(service.getAccount(404)).thenReturn(null);
        assertEquals(HttpStatus.NOT_FOUND,
                controller.accountOperation(404, 25, "deposit", null, null).getStatusCode());
        verify(service, never()).deposit(anyLong(), anyDouble());
    }

    @Test
    public void transferToUnknownAccountReturnsNotFoundWithoutMutating() {
        when(service.getAccount(100)).thenReturn(new AccountDTO(100));
        when(service.getAccount(404)).thenReturn(null);
        assertEquals(HttpStatus.NOT_FOUND,
                controller.accountOperation(100, 25, "transferFunds", 404L, "rent").getStatusCode());
        verify(service, never()).transferFunds(anyLong(), anyLong(), anyDouble(), any());
    }

    @Test
    public void depositOnExistingAccountReturnsOk() {
        when(service.getAccount(100)).thenReturn(new AccountDTO(100));
        assertEquals(HttpStatus.OK,
                controller.accountOperation(100, 25, "deposit", null, null).getStatusCode());
        verify(service).deposit(100, 25);
    }
}
