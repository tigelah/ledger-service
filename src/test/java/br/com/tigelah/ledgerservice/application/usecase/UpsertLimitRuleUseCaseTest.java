package br.com.tigelah.ledgerservice.application.usecase;


import br.com.tigelah.ledgerservice.domain.model.LimitRule;
import br.com.tigelah.ledgerservice.domain.model.LimitScopeType;
import br.com.tigelah.ledgerservice.domain.ports.LimitRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpsertLimitRuleUseCaseTest {

    @Test
    void should_upsert_rule_happy_path() {
        // arrange
        var repo = mock(LimitRuleRepository.class);
        var clock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);

        var uc = new UpsertLimitRuleUseCase(repo, clock);

        // repo retorna exatamente o que recebeu (simula persistência OK)
        when(repo.upsert(any(LimitRule.class))).thenAnswer(inv -> inv.getArgument(0));

        // act
        var out = uc.execute(
                LimitScopeType.USER,
                "user-123",
                "brl",
                100_00,
                50_00,
                200_00
        );

        // assert (retorno)
        assertNotNull(out);
        assertNotNull(out.id());
        assertEquals(LimitScopeType.USER, out.scopeType());
        assertEquals("user-123", out.scopeKey());
        assertEquals("BRL", out.currency()); // normaliza
        assertEquals(100_00, out.creditLimitCents());
        assertEquals(50_00, out.dailyLimitCents());
        assertEquals(200_00, out.monthlyLimitCents());
        assertEquals(Instant.parse("2030-01-01T00:00:00Z"), out.createdAt());
        assertEquals(Instant.parse("2030-01-01T00:00:00Z"), out.updatedAt());

        // assert (interação)
        var captor = ArgumentCaptor.forClass(LimitRule.class);
        verify(repo, times(1)).upsert(captor.capture());
        var saved = captor.getValue();

        assertNotNull(saved.id());
        assertEquals(out, saved); // se o repo devolve o mesmo objeto, deve bater
        verifyNoMoreInteractions(repo);
    }

    @Test
    void should_throw_when_scope_type_is_null() {
        var repo = mock(LimitRuleRepository.class);
        var clock = Clock.systemUTC();
        var uc = new UpsertLimitRuleUseCase(repo, clock);

        var ex = assertThrows(IllegalArgumentException.class, () ->
                uc.execute(null, "k", "BRL", 1, 0, 0)
        );
        assertEquals("scope_type_required", ex.getMessage());
        verifyNoInteractions(repo);
    }

    @ParameterizedTest
    @CsvSource({
            "'', scope_key_required",
            "'   ', scope_key_required"
    })
    void should_throw_when_scope_key_is_blank(String key, String expectedMessage) {
        var repo = mock(LimitRuleRepository.class);
        var clock = Clock.systemUTC();
        var uc = new UpsertLimitRuleUseCase(repo, clock);

        var ex = assertThrows(IllegalArgumentException.class, () ->
                uc.execute(LimitScopeType.PAN, key, "BRL", 1, 0, 0)
        );
        assertEquals(expectedMessage, ex.getMessage());
        verifyNoInteractions(repo);
    }

    @Test
    void should_throw_when_scope_key_is_null() {
        var repo = mock(LimitRuleRepository.class);
        var clock = Clock.systemUTC();
        var uc = new UpsertLimitRuleUseCase(repo, clock);

        var ex = assertThrows(IllegalArgumentException.class, () ->
                uc.execute(LimitScopeType.PAN, null, "BRL", 1, 0, 0)
        );
        assertEquals("scope_key_required", ex.getMessage());
        verifyNoInteractions(repo);
    }

    @ParameterizedTest
    @CsvSource({
            "'', currency_required",
            "'   ', currency_required"
    })
    void should_throw_when_currency_is_blank(String currency, String expectedMessage) {
        var repo = mock(LimitRuleRepository.class);
        var clock = Clock.systemUTC();
        var uc = new UpsertLimitRuleUseCase(repo, clock);

        var ex = assertThrows(IllegalArgumentException.class, () ->
                uc.execute(LimitScopeType.USER, "u1", currency, 1, 0, 0)
        );
        assertEquals(expectedMessage, ex.getMessage());
        verifyNoInteractions(repo);
    }

    @Test
    void should_throw_when_currency_is_null() {
        var repo = mock(LimitRuleRepository.class);
        var clock = Clock.systemUTC();
        var uc = new UpsertLimitRuleUseCase(repo, clock);

        var ex = assertThrows(IllegalArgumentException.class, () ->
                uc.execute(LimitScopeType.USER, "u1", null, 1, 0, 0)
        );
        assertEquals("currency_required", ex.getMessage());
        verifyNoInteractions(repo);
    }

    @ParameterizedTest(name = "credit={0}, daily={1}, monthly={2} -> must fail")
    @CsvSource({
            "-1, 0, 0",
            "0, -1, 0",
            "0, 0, -1",
            "-1, -1, 0",
            "0, -1, -1",
            "-1, 0, -1",
            "-1, -1, -1"
    })
    void should_throw_when_any_limit_is_negative(long credit, long daily, long monthly) {
        var repo = mock(LimitRuleRepository.class);
        var clock = Clock.systemUTC();
        var uc = new UpsertLimitRuleUseCase(repo, clock);

        var ex = assertThrows(IllegalArgumentException.class, () ->
                uc.execute(LimitScopeType.USER, "u1", "BRL", credit, daily, monthly)
        );
        assertEquals("limit_must_be_positive", ex.getMessage());
        verifyNoInteractions(repo);
    }

    @Test
    void should_propagate_repository_error() {
        var repo = mock(LimitRuleRepository.class);
        var clock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);
        var uc = new UpsertLimitRuleUseCase(repo, clock);

        when(repo.upsert(any(LimitRule.class))).thenThrow(new RuntimeException("db_down"));

        var ex = assertThrows(RuntimeException.class, () ->
                uc.execute(LimitScopeType.USER, "u1", "BRL", 10, 0, 0)
        );
        assertEquals("db_down", ex.getMessage());
        verify(repo, times(1)).upsert(any(LimitRule.class));
    }

    @Test
    void should_normalize_currency_to_uppercase() {
        var repo = mock(LimitRuleRepository.class);
        var clock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);
        var uc = new UpsertLimitRuleUseCase(repo, clock);

        when(repo.upsert(any(LimitRule.class))).thenAnswer(inv -> inv.getArgument(0));

        var out = uc.execute(LimitScopeType.USER, "u1", "bRl", 10, 0, 0);
        assertEquals("BRL", out.currency());
    }

    @Test
    void should_generate_new_uuid_every_time() {
        var repo = mock(LimitRuleRepository.class);
        var clock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);
        var uc = new UpsertLimitRuleUseCase(repo, clock);

        when(repo.upsert(any(LimitRule.class))).thenAnswer(inv -> inv.getArgument(0));

        var r1 = uc.execute(LimitScopeType.USER, "u1", "BRL", 10, 0, 0);
        var r2 = uc.execute(LimitScopeType.USER, "u1", "BRL", 10, 0, 0);

        assertNotEquals(r1.id(), r2.id());
    }
}
