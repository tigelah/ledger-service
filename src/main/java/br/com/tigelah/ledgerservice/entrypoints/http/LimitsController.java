package br.com.tigelah.ledgerservice.entrypoints.http;

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

    public LimitsController(UpsertLimitRuleUseCase upsert) {
        this.upsert = upsert;
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> upsertUser(@PathVariable String userId, @RequestBody LimitRuleRequest req) {
        var rule = upsert.execute(LimitScopeType.USER, userId, req.currency(), req.creditLimitCents(), req.dailyLimitCents(), req.monthlyLimitCents());
        return ResponseEntity.ok(LimitRuleResponse.from(rule));
    }

    @PutMapping("/pan/{panHash}")
    public ResponseEntity<?> upsertPan(@PathVariable String panHash, @RequestBody LimitRuleRequest req) {
        var rule = upsert.execute(LimitScopeType.PAN, panHash, req.currency(), req.creditLimitCents(), req.dailyLimitCents(), req.monthlyLimitCents());
        return ResponseEntity.ok(LimitRuleResponse.from(rule));
    }
}
