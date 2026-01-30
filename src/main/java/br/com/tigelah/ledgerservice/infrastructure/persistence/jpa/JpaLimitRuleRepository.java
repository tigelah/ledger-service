package br.com.tigelah.ledgerservice.infrastructure.persistence.jpa;

import br.com.tigelah.ledgerservice.infrastructure.persistence.entities.LimitRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaLimitRuleRepository  extends JpaRepository<LimitRuleEntity, UUID> {

    Optional<LimitRuleEntity> findByScopeTypeAndScopeKey(String scopeType, String scopeKey);
}
