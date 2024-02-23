package com.milko.user_provider;

import com.milko.user_provider.dto.input.ProfileHistoryInputDto;
import com.milko.user_provider.dto.output.ProfileHistoryOutputDto;
import com.milko.user_provider.model.ProfileHistory;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.model.User;
import com.milko.user_provider.repository.ProfileHistoryRepository;
import com.milko.user_provider.repository.UserRepository;
import com.milko.user_provider.service.impl.ProfileHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ProfileHistoryServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProfileHistoryRepository profileHistoryRepository;
    @InjectMocks
    private ProfileHistoryServiceImpl profileHistoryService;

    private ProfileHistoryInputDto historyInputDto;
    private User user;
    private ProfileHistory profileHistory;

    @BeforeEach
    public void init() {
        historyInputDto = ProfileHistoryInputDto.builder()
                .profileId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .build();
        user = User.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .secretKey("secretKey")
                .firstName("firstName")
                .lastName("lastName")
                .status(Status.ACTIVE)
                .build();
        profileHistory = ProfileHistory.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .profileId(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .reason("reason")
                .comment("comment")
                .changedValues("changed values")
                .build();
    }

    @Test
    public void createShouldReturnEmptyMono() {
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        Mockito.when(profileHistoryRepository.save(any(ProfileHistory.class))).thenReturn(Mono.just(profileHistory));
        Mono<Void> result = profileHistoryService.create(historyInputDto);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        Mockito.verify(userRepository).findById(any(UUID.class));
        Mockito.verify(profileHistoryRepository).save(any(ProfileHistory.class));
    }

    @Test
    public void createShouldThrowResponseStatusExceptionIfUserNotExists() {
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<Void> result = profileHistoryService.create(historyInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("User not exists"))
                .verify();
        Mockito.verify(userRepository).findById(any(UUID.class));
    }

    @Test
    public void getAllHistoryByProfileIdShouldReturnFluxOfProfileHistoryOutputDto() {
        Mockito.when(profileHistoryRepository.getAllByProfileId(any(UUID.class))).thenReturn(Flux.just(profileHistory));
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        Flux<ProfileHistoryOutputDto> result = profileHistoryService.getAllHistoryByProfileId(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(profileHistoryOutputDto -> {
                    return profileHistoryOutputDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            profileHistoryOutputDto.getProfile().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            profileHistoryOutputDto.getProfile().getSecretKey().equals("secretKey") &&
                            profileHistoryOutputDto.getProfile().getFirstName().equals("firstName") &&
                            profileHistoryOutputDto.getProfile().getLastName().equals("lastName") &&
                            profileHistoryOutputDto.getProfile().getStatus().equals(Status.ACTIVE) &&
                            profileHistoryOutputDto.getReason().equals("reason") &&
                            profileHistoryOutputDto.getComment().equals("comment") &&
                            profileHistoryOutputDto.getChangedValues().equals("changed values");
                })
                .expectComplete()
                .verify();
        Mockito.verify(profileHistoryRepository).getAllByProfileId(any(UUID.class));
        Mockito.verify(userRepository).findById(any(UUID.class));
    }

    @Test
    public void getAllHistoryByProfileIdShouldReturnEmptyFlux() {
        Mockito.when(profileHistoryRepository.getAllByProfileId(any(UUID.class))).thenReturn(Flux.empty());
        Flux<ProfileHistoryOutputDto> result = profileHistoryService.getAllHistoryByProfileId(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextCount(0)
                .expectComplete()
                .verify();
        Mockito.verify(profileHistoryRepository).getAllByProfileId(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnFluxOfProfileHistoryOutputDto() {
        Mockito.when(profileHistoryRepository.findById(any(UUID.class))).thenReturn(Mono.just(profileHistory));
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        Mono<ProfileHistoryOutputDto> result = profileHistoryService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(profileHistoryOutputDto -> {
                    return profileHistoryOutputDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            profileHistoryOutputDto.getProfile().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            profileHistoryOutputDto.getProfile().getSecretKey().equals("secretKey") &&
                            profileHistoryOutputDto.getProfile().getFirstName().equals("firstName") &&
                            profileHistoryOutputDto.getProfile().getLastName().equals("lastName") &&
                            profileHistoryOutputDto.getProfile().getStatus().equals(Status.ACTIVE) &&
                            profileHistoryOutputDto.getReason().equals("reason") &&
                            profileHistoryOutputDto.getComment().equals("comment") &&
                            profileHistoryOutputDto.getChangedValues().equals("changed values");
                })
                .expectComplete()
                .verify();
        Mockito.verify(profileHistoryRepository).findById(any(UUID.class));
        Mockito.verify(userRepository).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldThrowResponseStatusExceptionIfHistoryNotExists() {
        Mockito.when(profileHistoryRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<ProfileHistoryOutputDto> result = profileHistoryService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("History not exists"))
                .verify();
        Mockito.verify(profileHistoryRepository).findById(any(UUID.class));
    }




}
