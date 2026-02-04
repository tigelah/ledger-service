package br.com.tigelah.ledgerservice.application.handlers;

import br.com.tigelah.ledgerservice.domain.model.EntryType;
import br.com.tigelah.ledgerservice.domain.model.LedgerEntry;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class CaptureEventHandler {

    private final EntryRepository entries;

    public CaptureEventHandler(EntryRepository entries) {
        this.entries = entries;
    }
    
    public void onPaymentCaptured(
            UUID eventId,
            Instant occurredAt,
            String correlationId,
            UUID paymentId,
            UUID accountId,
            long principalCents,
            long interestCents,
            String currency
    ) {

        if (entries.existsEntryForPayment(accountId, paymentId, EntryType.CAPTURE_PRINCIPAL.name())) {
            return;
        }

        entries.append(LedgerEntry.debit(
                accountId,
                paymentId,
                EntryType.CAPTURE_PRINCIPAL,
                principalCents,
                currency,
                occurredAt,
                correlationId
        ));


        if (interestCents > 0) {
            entries.append(LedgerEntry.debit(
                    accountId,
                    paymentId,
                    EntryType.CAPTURE_INTEREST,
                    interestCents,
                    currency,
                    occurredAt,
                    correlationId
            ));
        }

        if (entries.existsHoldForPayment(accountId, paymentId)) {
            entries.append(LedgerEntry.credit(
                    accountId,
                    paymentId,
                    EntryType.RELEASE_HOLD,
                    principalCents,
                    currency,
                    occurredAt,
                    correlationId
            ));
        }
    }
}
