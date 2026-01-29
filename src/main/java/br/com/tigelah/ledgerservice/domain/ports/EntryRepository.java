package br.com.tigelah.ledgerservice.domain.ports;

import br.com.tigelah.ledgerservice.domain.model.LedgerEntry;

import java.util.List;
import java.util.UUID;

public interface EntryRepository {
    void append(LedgerEntry entry);
    List<LedgerEntry> findByAccountId(UUID accountId);
    long sumHoldDebits(UUID accountId);
    boolean existsHoldForPayment(UUID accountId, UUID paymentId);
}
