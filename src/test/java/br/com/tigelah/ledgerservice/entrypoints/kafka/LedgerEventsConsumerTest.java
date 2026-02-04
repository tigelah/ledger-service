package br.com.tigelah.ledgerservice.entrypoints.kafka;

import br.com.tigelah.ledgerservice.application.handlers.AuthorizationEventHandler;
import br.com.tigelah.ledgerservice.application.handlers.CaptureEventHandler;
import br.com.tigelah.ledgerservice.application.handlers.SettlementEventHandler;
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
        var settlementHandler = mock(SettlementEventHandler.class);

        var consumer = new LedgerEventsConsumer(mapper, authHandler, capHandler, settlementHandler);

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
          "currency":"BRL",
          "userId":"u1",
          "panHash":"h1"
        }
        """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        consumer.onMessage(msg);

        verify(capHandler, times(1)).onPaymentCaptured(
                any(), any(), eq("c1"),
                any(), any(),
                eq(1000L), eq(120L), eq("BRL")
        );
        verifyNoInteractions(authHandler);
        verifyNoInteractions(settlementHandler);
    }

    @Test
    void routes_settlement_completed_to_settlement_handler() {
        var mapper = new ObjectMapper().findAndRegisterModules();
        var authHandler = mock(AuthorizationEventHandler.class);
        var capHandler = mock(CaptureEventHandler.class);
        var settlementHandler = mock(SettlementEventHandler.class);

        var consumer = new LedgerEventsConsumer(mapper, authHandler, capHandler, settlementHandler);

        var msg = """
        {
          "eventId":"%s",
          "occurredAt":"2030-01-01T00:00:00Z",
          "correlationId":"c9",
          "type":"settlement.completed",
          "paymentId":"%s",
          "accountId":"%s",
          "currency":"BRL",
          "feeCents":32,
          "netCents":1088
        }
        """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        consumer.onMessage(msg);

        verify(settlementHandler, times(1)).onSettlementCompleted(
                any(), any(), eq("BRL"),
                eq(32L), eq(1088L),
                eq("c9"), any()
        );
        verifyNoInteractions(authHandler);
        verifyNoInteractions(capHandler);
    }
}