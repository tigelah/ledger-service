package br.com.tigelah.ledgerservice.infrastructure.config;

import br.com.tigelah.ledgerservice.application.handlers.AuthorizationEventHandler;
import br.com.tigelah.ledgerservice.application.usecase.CreateAccountUseCase;
import br.com.tigelah.ledgerservice.application.usecase.GetAvailableCreditUseCase;
import br.com.tigelah.ledgerservice.application.usecase.RecordHoldFromAuthorizationUseCase;
import br.com.tigelah.ledgerservice.domain.ports.AccountRepository;
import br.com.tigelah.ledgerservice.domain.ports.EntryRepository;
import br.com.tigelah.ledgerservice.domain.ports.EventPublisher;
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
}