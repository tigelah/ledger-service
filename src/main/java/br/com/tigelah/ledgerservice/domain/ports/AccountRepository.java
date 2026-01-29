package br.com.tigelah.ledgerservice.domain.ports;

import br.com.tigelah.ledgerservice.domain.model.LedgerAccount;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Optional<LedgerAccount> findById(UUID id);
    void save(LedgerAccount account);
}
