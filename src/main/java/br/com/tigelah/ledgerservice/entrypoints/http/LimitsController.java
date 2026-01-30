package br.com.tigelah.ledgerservice.entrypoints.http;

import br.com.tigelah.ledgerservice.application.usecase.GetLimitRuleUseCase;
import br.com.tigelah.ledgerservice.application.usecase.UpsertLimitRuleUseCase;
import br.com.tigelah.ledgerservice.domain.model.LimitScopeType;
import br.com.tigelah.ledgerservice.entrypoints.http.dto.LimitRuleRequest;
import br.com.tigelah.ledgerservice.entrypoints.http.dto.LimitRuleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/limits")
public class LimitsController {

    private final UpsertLimitRuleUseCase upsert;
    private final GetLimitRuleUseCase get;

    public LimitsController(UpsertLimitRuleUseCase upsert, GetLimitRuleUseCase get) {
        this.upsert = upsert;
        this.get = get;
    }

    @PutMapping("/pan/{panHash}")
    public ResponseEntity<?> upsertPan(@PathVariable String panHash, @RequestBody LimitRuleRequest req) {
        return doUpsert(LimitScopeType.PAN, panHash, req);
    }

    @GetMapping("/pan/{panHash}")
    public ResponseEntity<?> getPan(@PathVariable String panHash) {
        return doGet(LimitScopeType.PAN, panHash);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> upsertUser(@PathVariable String userId, @RequestBody LimitRuleRequest req) {
        return doUpsert(LimitScopeType.USER, userId, req);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        return doGet(LimitScopeType.USER, userId);
    }

    private ResponseEntity<?> doUpsert(LimitScopeType type, String key, LimitRuleRequest req) {
        if (req == null) return ResponseEntity.badRequest().body("payload_required");
        try {
            var currency = (req.currency() == null || req.currency().isBlank()) ? "BRL" : req.currency();
            var out = upsert.execute(type, key, currency, req.creditLimitCents(), req.dailyLimitCents(), req.monthlyLimitCents());
            return ResponseEntity.ok(LimitRuleResponse.from(out));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ResponseEntity<?> doGet(LimitScopeType type, String key) {
        try {
            var out = get.execute(type, key);
            return out.<ResponseEntity<?>>map(r -> ResponseEntity.ok(LimitRuleResponse.from(r)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

