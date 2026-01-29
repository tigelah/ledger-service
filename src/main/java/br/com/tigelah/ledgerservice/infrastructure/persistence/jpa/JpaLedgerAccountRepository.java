package br.com.tigelah.ledgerservice.infrastructure.persistence.jpa;

import br.com.tigelah.ledgerservice.infrastructure.persistence.entities.LedgerAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaLedgerAccountRepository extends JpaRepository<LedgerAccountEntity, UUID> {}