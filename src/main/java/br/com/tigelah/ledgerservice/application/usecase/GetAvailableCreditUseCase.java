package br.com.tigelah.ledgerservice.application.usecase;

import br.com.tigelah.ledgerservice.domain.ports.AccountRepository;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;

import java.util.UUID;

public class GetAvailableCreditUseCase {

    private final AccountRepository accounts;
    private final EntryRepository entries;

    public GetAvailableCreditUseCase(AccountRepository accounts, EntryRepository entries) {
        this.accounts = accounts;
        this.entries = entries;
    }

    public AvailableCredit execute(UUID accountId) {
        var acc = accounts.findById(accountId).orElseThrow(() -> new IllegalArgumentException("account_not_found"));

        long holds = entries.sumHoldDebits(accountId);
        long captures = entries.sumCaptureDebits(accountId);

        long available = acc.creditLimitCents() - holds - captures;
        if (available < 0) available = 0;

        return new AvailableCredit(acc.id(), available, acc.currency(), holds, captures);
    }

}
