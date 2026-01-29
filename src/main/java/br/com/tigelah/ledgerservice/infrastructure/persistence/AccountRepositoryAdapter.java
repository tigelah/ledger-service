package br.com.tigelah.ledgerservice.infrastructure.persistence;

import br.com.tigelah.ledgerservice.domain.model.LedgerAccount;
import br.com.tigelah.ledgerservice.domain.ports.AccountRepository;
import br.com.tigelah.ledgerservice.infrastructure.persistence.entities.LedgerAccountEntity;
import br.com.tigelah.ledgerservice.infrastructure.persistence.jpa.JpaLedgerAccountRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountRepositoryAdapter implements AccountRepository {
    private final JpaLedgerAccountRepository repo;

    public AccountRepositoryAdapter(JpaLedgerAccountRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<LedgerAccount> findById(UUID id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public void save(LedgerAccount account) {
        var e = repo.findById(account.id()).orElseGet(LedgerAccountEntity::new);
        e.id = account.id();
        e.creditLimitCents = account.creditLimitCents();
        e.currency = account.currency();
        e.createdAt = account.createdAt();
        repo.save(e);
    }

    private LedgerAccount toDomain(LedgerAccountEntity e) {
        return new LedgerAccount(e.id, e.creditLimitCents, e.currency, e.createdAt);
    }
}
