package br.com.tigelah.ledgerservice.application.usecase;

import br.com.tigelah.ledgerservice.domain.model.*;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;
import br.com.tigelah.ledgerservice.domain.ports.SpendCounterRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RecordCaptureUseCaseTest {

    @Test
    void records_capture_release_and_updates_counters() {
        var entriesList = new ArrayList<LedgerEntry>();
        EntryRepository entries = new EntryRepository() {
            boolean capturedExists = false;
            @Override public void append(LedgerEntry entry) {
                entriesList.add(entry);
                if (entry.entryType() == EntryType.CAPTURE) capturedExists = true;
            }


            @Override public boolean existsEntryForPayment(UUID accountId, UUID paymentId, String entryType) {
                return capturedExists && "CAPTURE".equals(entryType);
            }
            @Override public long sumHoldDebits(UUID accountId) { return 0; }

            @Override
            public boolean existsHoldForPayment(UUID accountId, UUID paymentId) {
                return false;
            }

            @Override public long sumCaptureDebits(UUID accountId) { return 0; }
        };

        var counterWrites = new AtomicInteger(0);
        SpendCounterRepository counters = new SpendCounterRepository() {
            private final Map<String, SpendCounter> store = new HashMap<>();
            @Override public Optional<SpendCounter> find(LimitScopeType s, String k, PeriodType p, Instant start) {
                return Optional.ofNullable(store.get(s+"|"+k+"|"+p+"|"+start));
            }
            @Override public SpendCounter save(SpendCounter c) {
                store.put(c.scopeType()+"|"+c.scopeKey()+"|"+c.periodType()+"|"+c.periodStart(), c);
                counterWrites.incrementAndGet();
                return c;
            }
        };

        var clock = Clock.fixed(Instant.parse("2030-01-02T10:00:00Z"), ZoneOffset.UTC);
        var uc = new RecordCaptureUseCase(entries, counters, clock);

        var r = uc.execute(UUID.randomUUID(), UUID.randomUUID(), 10, "BRL", "c1", LimitScopeType.USER, "u1");
        assertTrue(r.recorded());
        assertFalse(r.idempotent());

        assertEquals(2, entriesList.size());
        assertEquals(EntryType.CAPTURE, entriesList.get(0).entryType());
        assertEquals(EntryType.RELEASE_HOLD, entriesList.get(1).entryType());
        assertEquals(2, counterWrites.get()); // day + month
    }

    @Test
    void idempotent_when_capture_already_exists() {
        EntryRepository entries = new EntryRepository() {
            @Override public void append(LedgerEntry entry) { fail("should_not_append"); }
            @Override public boolean existsEntryForPayment(UUID accountId, UUID paymentId, String entryType) { return true; }
            @Override public long sumHoldDebits(UUID accountId) { return 0; }

            @Override
            public boolean existsHoldForPayment(UUID accountId, UUID paymentId) {
                return false;
            }

            @Override public long sumCaptureDebits(UUID accountId) { return 0; }
        };

        SpendCounterRepository counters = new SpendCounterRepository() {
            @Override public Optional<SpendCounter> find(LimitScopeType scopeType, String scopeKey, PeriodType periodType, Instant periodStart) { return Optional.empty(); }
            @Override public SpendCounter save(SpendCounter counter) { fail("should_not_save_counter"); return counter; }
        };

        var clock = Clock.fixed(Instant.parse("2030-01-02T10:00:00Z"), ZoneOffset.UTC);
        var uc = new RecordCaptureUseCase(entries, counters, clock);

        var r = uc.execute(UUID.randomUUID(), UUID.randomUUID(), 10, "BRL", "c1", LimitScopeType.USER, "u1");
        assertFalse(r.recorded());
        assertTrue(r.idempotent());
    }
}
