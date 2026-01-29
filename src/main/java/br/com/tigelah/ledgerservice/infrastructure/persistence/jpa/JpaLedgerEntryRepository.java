package br.com.tigelah.ledgerservice.infrastructure.persistence.jpa;

import br.com.tigelah.ledgerservice.infrastructure.persistence.entities.LedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JpaLedgerEntryRepository extends JpaRepository<LedgerEntryEntity, UUID> {

    List<LedgerEntryEntity> findByAccountId(UUID accountId);

    boolean existsByAccountIdAndPaymentIdAndEntryType(UUID accountId, UUID paymentId, String entryType);

    @Query("""
      select coalesce(sum(e.amountCents),0)
      from LedgerEntryEntity e
      where e.accountId = :accountId
        and e.entryType = 'HOLD'
        and e.direction = 'DEBIT'
      """)
    long sumHoldDebits(UUID accountId);
}
