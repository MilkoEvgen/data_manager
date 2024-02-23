package com.milko.user_provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milko.user_provider.dto.input.IndividualsInputDto;
import com.milko.user_provider.dto.input.ProfileHistoryInputDto;
import com.milko.user_provider.dto.output.IndividualsOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.Individuals;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.repository.IndividualsRepository;
import com.milko.user_provider.service.ProfileHistoryService;
import com.milko.user_provider.service.UserService;
import com.milko.user_provider.service.impl.IndividualsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class IndividualsServiceImplTest {
    @Mock
    private IndividualsRepository individualsRepository;
    @Mock
    private UserService userService;
    @Mock
    private ProfileHistoryService profileHistoryService;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private IndividualsServiceImpl individualsService;

    private IndividualsInputDto individualInputDto;
    private UserOutputDto userOutputDto;
    private Individuals individual;

    @BeforeEach
    public void init(){
        individualInputDto = IndividualsInputDto.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .build();
        userOutputDto = UserOutputDto.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .secretKey("secretKey")
                .firstName("firstName")
                .lastName("lastName")
                .status(Status.ACTIVE)
                .build();
        individual = Individuals.builder()
                .id(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .passportNumber("passportNumber")
                .phoneNumber("phoneNumber")
                .email("email")
                .userId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    public void createShouldReturnIndividualsOutputDto(){
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(individualsRepository.save(any(Individuals.class))).thenReturn(Mono.just(individual));
        Mono<IndividualsOutputDto> result = individualsService.create(individualInputDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getUser().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getUser().getSecretKey().equals("secretKey") &&
                            resultDto.getUser().getFirstName().equals("firstName") &&
                            resultDto.getUser().getLastName().equals("lastName") &&
                            resultDto.getUser().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getPassportNumber().equals("passportNumber") &&
                            resultDto.getPhoneNumber().equals("phoneNumber") &&
                            resultDto.getEmail().equals("email") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(individualsRepository).save(any(Individuals.class));
    }

    @Test
    public void createShouldThrowResponseStatusExceptionIfUserNotExists(){
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<IndividualsOutputDto> result = individualsService.create(individualInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("User not exists"))
                .verify();
        Mockito.verify(userService).findById(any(UUID.class));
    }

    @Test
    public void updateShouldReturnIndividualsOutputDto(){
        Mockito.when(individualsRepository.findById(any(UUID.class))).thenReturn(Mono.just(individual));
        Mockito.when(profileHistoryService.create(any(ProfileHistoryInputDto.class))).thenReturn(Mono.empty());
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(individualsRepository.save(any(Individuals.class))).thenReturn(Mono.just(individual));
        Mono<IndividualsOutputDto> result = individualsService.update(UUID.randomUUID(), individualInputDto, "reason", "comment");
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getUser().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getUser().getSecretKey().equals("secretKey") &&
                            resultDto.getUser().getFirstName().equals("firstName") &&
                            resultDto.getUser().getLastName().equals("lastName") &&
                            resultDto.getUser().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getPassportNumber().equals("passportNumber") &&
                            resultDto.getPhoneNumber().equals("phoneNumber") &&
                            resultDto.getEmail().equals("email") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(individualsRepository).findById(any(UUID.class));
        Mockito.verify(profileHistoryService).create(any(ProfileHistoryInputDto.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(individualsRepository).save(any(Individuals.class));
    }

    @Test
    public void updateShouldThrowResponseStatusExceptionIfIndividualNotExists(){
        Mockito.when(individualsRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<IndividualsOutputDto> result = individualsService.update(UUID.randomUUID(), individualInputDto, "reason", "comment");
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Individual not exists"))
                .verify();
        Mockito.verify(individualsRepository).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnIndividualsOutputDto(){
        Mockito.when(individualsRepository.findById(any(UUID.class))).thenReturn(Mono.just(individual));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mono<IndividualsOutputDto> result = individualsService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getUser().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getUser().getSecretKey().equals("secretKey") &&
                            resultDto.getUser().getFirstName().equals("firstName") &&
                            resultDto.getUser().getLastName().equals("lastName") &&
                            resultDto.getUser().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getPassportNumber().equals("passportNumber") &&
                            resultDto.getPhoneNumber().equals("phoneNumber") &&
                            resultDto.getEmail().equals("email") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(individualsRepository).findById(any(UUID.class));
        Mockito.verify(userService).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldThrowResponseStatusExceptionIfIndividualNotExists(){
        Mockito.when(individualsRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<IndividualsOutputDto> result = individualsService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Individual not exists"))
                .verify();
        Mockito.verify(individualsRepository).findById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldReturnTrue(){
        Mockito.when(individualsRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.just(true));
        Mono<Boolean> result = individualsService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify();
        Mockito.verify(individualsRepository).updateStatusToDeletedById(any(UUID.class));
    }


    @Test
    public void deleteByIdShouldThrowResponseStatusExceptionIfIndividualNotExists(){
        Mockito.when(individualsRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.just(false));
        Mono<Boolean> result = individualsService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Individual not exists"))
                .verify();
        Mockito.verify(individualsRepository).updateStatusToDeletedById(any(UUID.class));
    }


}
