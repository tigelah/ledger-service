package br.com.tigelah.ledgerservice.entrypoints.http;

import br.com.tigelah.ledgerservice.application.usecase.GetAvailableCreditUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    private final GetAvailableCreditUseCase availableCredit;

    public AccountsController(GetAvailableCreditUseCase availableCredit) {
        this.availableCredit = availableCredit;
    }

    @GetMapping("/{accountId}/available-credit")
    public ResponseEntity<?> getAvailable(@PathVariable UUID accountId) {
        try {
            var r = availableCredit.execute(accountId);
            return ResponseEntity.ok(new AvailableCreditResponse(r.accountId(), r.availableCents(), r.currency(), r.holdsCents(), r.capturedCents()));
        } catch (IllegalArgumentException e) {
            if ("account_not_found".equals(e.getMessage())) return ResponseEntity.notFound().build();
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }

    public record AvailableCreditResponse(UUID accountId, long availableCents, String currency, long holdsCents, long capturedCents) {}
}
