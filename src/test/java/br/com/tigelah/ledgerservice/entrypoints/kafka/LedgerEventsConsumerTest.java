package br.com.tigelah.ledgerservice.entrypoints.kafka;

import br.com.tigelah.ledgerservice.application.handlers.AuthorizationEventHandler;
import br.com.tigelah.ledgerservice.application.handlers.CaptureEventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class LedgerEventsConsumerTest {

    @Test
    void routes_payment_captured_to_capture_handler() {
        var mapper = new ObjectMapper().findAndRegisterModules();
        var authHandler = mock(AuthorizationEventHandler.class);
        var capHandler = mock(CaptureEventHandler.class);

        var consumer = new LedgerEventsConsumer(mapper, authHandler, capHandler);

        var msg = """
        {
          "eventId":"%s",
          "occurredAt":"2030-01-01T00:00:00Z",
          "correlationId":"c1",
          "type":"payment.captured",
          "paymentId":"%s",
          "accountId":"%s",
          "amountCents":1000,
          "interestCents":120,
          "currency":"BRL"
        }
        """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        consumer.onMessage(msg);

        verify(capHandler, times(1)).onPaymentCaptured(
                any(), any(), eq("c1"),
                any(), any(),
                eq(1000L), eq(120L), eq("BRL")
        );
        verifyNoInteractions(authHandler);
    }
}