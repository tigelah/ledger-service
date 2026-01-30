package br.com.tigelah.ledgerservice.infrastructure.config;

import br.com.tigelah.ledgerservice.application.handlers.AuthorizationEventHandler;
import br.com.tigelah.ledgerservice.application.usecase.*;
import br.com.tigelah.ledgerservice.domain.ports.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UseCasesConfig {

    @Bean
    CreateAccountUseCase createAccountUseCase(AccountRepository accounts, Clock clock) {
        return new CreateAccountUseCase(accounts, clock);
    }

    @Bean
    GetAvailableCreditUseCase getAvailableCreditUseCase(AccountRepository accounts, EntryRepository entries) {
        return new GetAvailableCreditUseCase(accounts, entries);
    }

    @Bean
    RecordHoldFromAuthorizationUseCase recordHoldFromAuthorizationUseCase(AccountRepository accounts, EntryRepository entries, Clock clock) {
        return new RecordHoldFromAuthorizationUseCase(accounts, entries, clock);
    }

    @Bean
    AuthorizationEventHandler authorizationEventHandler(RecordHoldFromAuthorizationUseCase uc, EventPublisher events, ObjectMapper mapper) {
        return new AuthorizationEventHandler(uc, events, mapper);
    }

    @Bean
    UpsertLimitRuleUseCase upsertLimitRuleUseCase(LimitRuleRepository repo, Clock clock) {
        return new UpsertLimitRuleUseCase(repo, clock);
    }

    @Bean
    GetLimitRuleUseCase getLimitRuleUseCase(LimitRuleRepository repo) {
        return new GetLimitRuleUseCase(repo);
    }

    @Bean
    RecordCaptureUseCase recordCaptureUseCase(EntryRepository entries, SpendCounterRepository counters, Clock clock) {
        return new RecordCaptureUseCase(entries, counters, clock);
    }
}