package br.com.tigelah.ledgerservice.infrastructure.persistence;

import br.com.tigelah.ledgerservice.domain.model.*;
import br.com.tigelah.ledgerservice.domain.ports.SpendCounterRepository;
import br.com.tigelah.ledgerservice.infrastructure.persistence.entities.SpendCounterEntity;
import br.com.tigelah.ledgerservice.infrastructure.persistence.entities.SpendCounterId;
import br.com.tigelah.ledgerservice.infrastructure.persistence.jpa.JpaSpendCounterRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class SpendCounterRepositoryAdapter implements SpendCounterRepository {
    private final JpaSpendCounterRepository repo;

    public SpendCounterRepositoryAdapter(JpaSpendCounterRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<SpendCounter> find(LimitScopeType scopeType, String scopeKey, PeriodType periodType, Instant periodStart) {
        var id = new SpendCounterId();
        id.scopeType = scopeType.name();
        id.scopeKey = scopeKey;
        id.periodType = periodType.name();
        id.periodStart = periodStart;

        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public SpendCounter save(SpendCounter counter) {
        var e = new SpendCounterEntity();
        e.scopeType = counter.scopeType().name();
        e.scopeKey = counter.scopeKey();
        e.periodType = counter.periodType().name();
        e.periodStart = counter.periodStart();
        e.currency = counter.currency();
        e.amountCents = counter.amountCents();
        e.updatedAt = counter.updatedAt();
        repo.save(e);
        return counter;
    }

    private SpendCounter toDomain(SpendCounterEntity e) {
        return new SpendCounter(LimitScopeType.valueOf(e.scopeType), e.scopeKey, PeriodType.valueOf(e.periodType),
                e.periodStart, e.currency, e.amountCents, e.updatedAt);
    }
}
