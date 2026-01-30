package br.com.tigelah.ledgerservice.infrastructure.persistence;

import br.com.tigelah.ledgerservice.domain.model.EntryDirection;
import br.com.tigelah.ledgerservice.domain.model.EntryType;
import br.com.tigelah.ledgerservice.domain.model.LedgerEntry;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;
import br.com.tigelah.ledgerservice.infrastructure.persistence.entities.LedgerEntryEntity;
import br.com.tigelah.ledgerservice.infrastructure.persistence.jpa.JpaLedgerEntryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class EntryRepositoryAdapter implements EntryRepository {
    private final JpaLedgerEntryRepository repo;

    public EntryRepositoryAdapter(JpaLedgerEntryRepository repo) {
        this.repo = repo;
    }

    @Override
    public void append(LedgerEntry entry) {
        var e = new LedgerEntryEntity();
        e.id = entry.id();
        e.accountId = entry.accountId();
        e.paymentId = entry.paymentId();
        e.entryType = entry.entryType().name();
        e.direction = entry.direction().name();
        e.amountCents = entry.amountCents();
        e.currency = entry.currency();
        e.occurredAt = entry.occurredAt();
        e.correlationId = entry.correlationId();
        repo.save(e);
    }
    @Override
    public boolean existsHoldForPayment(UUID accountId, UUID paymentId) {
        return repo.existsByAccountIdAndPaymentIdAndEntryType(accountId, paymentId, EntryType.HOLD.name());
    }

    @Override
    public boolean existsEntryForPayment(UUID accountId, UUID paymentId, String entryType) {
        return false;
    }


    private LedgerEntry toDomain(LedgerEntryEntity e) {
        return new LedgerEntry(
                e.id, e.accountId, e.paymentId,
                EntryType.valueOf(e.entryType),
                EntryDirection.valueOf(e.direction),
                e.amountCents, e.currency, e.occurredAt, e.correlationId
        );
    }

    @Override
    public long sumHoldDebits(UUID accountId) {
        return repo.sumHoldDebits(accountId);
    }

    @Override
    public long sumCaptureDebits(UUID accountId) {
        return repo.sumCaptureDebits(accountId);
    }
}
