package br.com.tigelah.ledgerservice.application.usecase;

import br.com.tigelah.ledgerservice.domain.model.LedgerAccount;
import br.com.tigelah.ledgerservice.domain.ports.AccountRepository;

import java.time.Clock;
import java.util.UUID;

public class CreateAccountUseCase {
    private final AccountRepository accounts;
    private final Clock clock;

    public CreateAccountUseCase(AccountRepository accounts, Clock clock) {
        this.accounts = accounts;
        this.clock = clock;
    }

    public LedgerAccount execute(UUID accountId, long creditLimitCents, String currency) {
        if (accountId == null) throw new IllegalArgumentException("accountId_required");
        if (creditLimitCents < 0) throw new IllegalArgumentException("credit_limit_must_be_positive");
        if (currency == null || currency.isBlank()) throw new IllegalArgumentException("currency_required");

        var account = new LedgerAccount(accountId, creditLimitCents, currency.toUpperCase(), clock.instant());
        accounts.save(account);
        return account;
    }
}
