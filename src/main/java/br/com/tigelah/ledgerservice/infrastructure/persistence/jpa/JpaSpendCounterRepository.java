package br.com.tigelah.ledgerservice.infrastructure.persistence.jpa;

import br.com.tigelah.ledgerservice.infrastructure.persistence.entities.SpendCounterEntity;
import br.com.tigelah.ledgerservice.infrastructure.persistence.entities.SpendCounterId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSpendCounterRepository extends JpaRepository<SpendCounterEntity, SpendCounterId> {}
