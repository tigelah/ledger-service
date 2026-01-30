package br.com.tigelah.ledgerservice.entrypoints.http;

import br.com.tigelah.ledgerservice.application.usecase.CreateAccountUseCase;
import br.com.tigelah.ledgerservice.application.usecase.GetAvailableCreditUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LedgerAccountsControllerTest {

    @Test
    void create_bad_request_when_missing_accountId() {
        var create = Mockito.mock(CreateAccountUseCase.class);
        var avail = Mockito.mock(GetAvailableCreditUseCase.class);

        var c = new LedgerAccountsController(create, avail);
        var resp = c.create(null);

        assertEquals(400, resp.getStatusCodeValue());
    }

    @Test
    void get_available_returns_200() {
        var create = Mockito.mock(CreateAccountUseCase.class);
        var avail = Mockito.mock(GetAvailableCreditUseCase.class);

        var accountId = UUID.randomUUID();
        Mockito.when(avail.execute(accountId))
                .thenReturn(new GetAvailableCreditUseCase.AvailableCredit.execute(accountId, 90, "BRL"));

        var c = new LedgerAccountsController(create, avail);
        var resp = c.getAvailable(accountId);

        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
    }

    @Test
    void get_available_returns_404_when_not_found() {
        var create = Mockito.mock(CreateAccountUseCase.class);
        var avail = Mockito.mock(GetAvailableCreditUseCase.class);

        var accountId = UUID.randomUUID();
        Mockito.when(avail.execute(accountId)).thenThrow(new IllegalArgumentException("account_not_found"));

        var c = new LedgerAccountsController(create, avail);
        var resp = c.getAvailable(accountId);

        assertEquals(404, resp.getStatusCodeValue());
    }
}

