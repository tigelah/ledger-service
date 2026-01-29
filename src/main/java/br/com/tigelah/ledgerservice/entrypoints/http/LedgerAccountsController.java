package br.com.tigelah.ledgerservice.entrypoints.http;

import br.com.tigelah.ledgerservice.application.usecase.CreateAccountUseCase;
import br.com.tigelah.ledgerservice.application.usecase.GetAvailableCreditUseCase;
import br.com.tigelah.ledgerservice.entrypoints.http.dto.AvailableCreditResponse;
import br.com.tigelah.ledgerservice.entrypoints.http.dto.CreateAccountRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class LedgerAccountsController {
    private final CreateAccountUseCase createAccount;
    private final GetAvailableCreditUseCase availableCredit;

    public LedgerAccountsController(CreateAccountUseCase createAccount, GetAvailableCreditUseCase availableCredit) {
        this.createAccount = createAccount;
        this.availableCredit = availableCredit;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateAccountRequest req) {
        if (req == null || req.accountId() == null) return ResponseEntity.badRequest().body("accountId_required");
        var currency = (req.currency() == null || req.currency().isBlank()) ? "BRL" : req.currency();
        var acc = createAccount.execute(req.accountId(), req.creditLimitCents(), currency);
        return ResponseEntity.ok(new AvailableCreditResponse(acc.id(), acc.creditLimitCents(), acc.currency()));
    }

    @GetMapping("/{accountId}/available-credit")
    public ResponseEntity<?> getAvailable(@PathVariable UUID accountId) {
        try {
            var r = availableCredit.execute(accountId);
            return ResponseEntity.ok(new AvailableCreditResponse(r.accountId(), r.availableCents(), r.currency()));
        } catch (IllegalArgumentException e) {
            if ("account_not_found".equals(e.getMessage())) return ResponseEntity.notFound().build();
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }
}
