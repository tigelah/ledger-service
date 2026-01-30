package br.com.tigelah.ledgerservice.application.usecase;

import br.com.tigelah.ledgerservice.application.time.PeriodBuckets;
import br.com.tigelah.ledgerservice.domain.model.*;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;
import br.com.tigelah.ledgerservice.domain.ports.SpendCounterRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class RecordCaptureUseCase {

    private final EntryReposzitory entries;
    private final SpendCounterRepository counters;
    private final Clock clock;

    public RecordCaptureUseCase(EntryRepository entries, SpendCounterRepository counters, Clock clock) {
        this.entries = entries;
        this.counters = counters;
        this.clock = clock;
    }

    public Result execute(UUID accountId,
                          UUID paymentId,
                          long amountCents,
                          String currency,
                          String correlationId,
                          LimitScopeType scopeType,
                          String scopeKey) {
        if (accountId == null) throw new IllegalArgumentException("account_id_required");
        if (paymentId == null) throw new IllegalArgumentException("payment_id_required");
        if (amountCents <= 0) throw new IllegalArgumentException("amount_must_be_positive");
        if (currency == null || currency.isBlank()) throw new IllegalArgumentException("currency_required");
        if (scopeType == null) throw new IllegalArgumentException("scope_type_required");
        if (scopeKey == null || scopeKey.isBlank()) throw new IllegalArgumentException("scope_key_required");


        if (entries.existsEntryForPayment(accountId, paymentId, EntryType.CAPTURE.name())) {
            return new Result(false, true);
        }

        Instant now = clock.instant();

        entries.append(new LedgerEntry(
                UUID.randomUUID(),
                accountId,
                paymentId,
                EntryType.CAPTURE,
                EntryDirection.DEBIT,
                amountCents,
                currency.toUpperCase(),
                now,
                correlationId
        ));


        entries.append(new LedgerEntry(
                UUID.randomUUID(),
                accountId,
                paymentId,
                EntryType.RELEASE_HOLD,
                EntryDirection.CREDIT,
                amountCents,
                currency.toUpperCase(),
                now,
                correlationId
        ));


        bump(scopeType, scopeKey, PeriodType.DAY, PeriodBuckets.dayStartUtc(now), currency, amountCents, now);
        bump(scopeType, scopeKey, PeriodType.MONTH, PeriodBuckets.monthStartUtc(now), currency, amountCents, now);

        return new Result(true, false);
    }

    private void bump(LimitScopeType scopeType, String scopeKey, PeriodType periodType, Instant periodStart,
                      String currency, long delta, Instant now) {
        var existing = counters.find(scopeType, scopeKey, periodType, periodStart);
        var updated = existing
                .map(c -> new SpendCounter(scopeType, scopeKey, periodType, periodStart, currency.toUpperCase(), c.amountCents() + delta, now))
                .orElseGet(() -> new SpendCounter(scopeType, scopeKey, periodType, periodStart, currency.toUpperCase(), delta, now));

        counters.save(updated);
    }
}
