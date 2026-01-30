package br.com.tigelah.ledgerservice.domain.ports;

import br.com.tigelah.ledgerservice.domain.model.EntryType;
import br.com.tigelah.ledgerservice.domain.model.LedgerEntry;

import java.util.List;
import java.util.UUID;

public interface EntryRepository {
    void append(LedgerEntry entry);
    long sumHoldDebits(UUID accountId);
    boolean existsHoldForPayment(UUID accountId, UUID paymentId);
    boolean existsEntryForPayment(UUID accountId, UUID paymentId, String entryType);
    long sumCaptureDebits(UUID accountId);
}
