package br.com.tigelah.ledgerservice.infrastructure.persistence.jpa;

import br.com.tigelah.ledgerservice.infrastructure.persistence.entities.LedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaLedgerEntryRepository extends JpaRepository<LedgerEntryEntity, UUID> {

    boolean existsByAccountIdAndPaymentIdAndEntryType(UUID accountId, UUID paymentId, String entryType);
    @Query("""
     select coalesce(sum(e.amountCents), 0)
     from LedgerEntryEntity e
     where e.accountId = :accountId
       and e.entryType = 'HOLD'
       and e.direction = 'DEBIT'
  """)
    long sumHoldDebits(@Param("accountId") UUID accountId);

    @Query("""
     select coalesce(sum(e.amountCents), 0)
     from LedgerEntryEntity e
     where e.accountId = :accountId
       and e.entryType = 'CAPTURE'
       and e.direction = 'DEBIT'
  """)
    long sumCaptureDebits(@Param("accountId") UUID accountId);
}
