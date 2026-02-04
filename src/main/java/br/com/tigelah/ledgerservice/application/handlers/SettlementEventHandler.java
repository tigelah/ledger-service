package br.com.tigelah.ledgerservice.application.handlers;

import br.com.tigelah.ledgerservice.domain.model.EntryType;
import br.com.tigelah.ledgerservice.domain.model.LedgerEntry;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Component
public class SettlementEventHandler {

    private final EntryRepository entries;
    private final Clock clock;

    public SettlementEventHandler(EntryRepository entries, Clock clock) {
        this.entries = entries;
        this.clock = clock;
    }

    public void onSettlementCompleted(UUID paymentId,
                                      UUID accountId,
                                      String currency,
                                      long feeCents,
                                      long netCents,
                                      String correlationId,
                                      Instant occurredAt) {

        if (entries.existsEntryForPayment(accountId, paymentId, EntryType.SETTLEMENT_NET.name())) {
            return;
        }

        Instant when = occurredAt != null ? occurredAt : clock.instant();
        String cur = currency == null || currency.isBlank() ? "BRL" : currency.toUpperCase();

        if (feeCents > 0) {
            entries.append(LedgerEntry.debit(
                    accountId,
                    paymentId,
                    EntryType.SETTLEMENT_FEE,
                    feeCents,
                    cur,
                    when,
                    correlationId == null ? "n/a" : correlationId
            ));
        }

        if (netCents <= 0) {
            throw new IllegalArgumentException("net_must_be_positive");
        }

        entries.append(LedgerEntry.credit(
                accountId,
                paymentId,
                EntryType.SETTLEMENT_NET,
                netCents,
                cur,
                when,
                correlationId == null ? "n/a" : correlationId
        ));
    }
}
