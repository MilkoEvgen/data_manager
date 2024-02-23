package com.milko.user_provider;

import com.milko.user_provider.dto.input.VerificationStatusInputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.dto.output.VerificationStatusOutputDto;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.model.StatusOfVerification;
import com.milko.user_provider.model.User;
import com.milko.user_provider.model.VerificationStatus;
import com.milko.user_provider.repository.UserRepository;
import com.milko.user_provider.repository.VerificationStatusRepository;
import com.milko.user_provider.service.UserService;
import com.milko.user_provider.service.impl.VerificationStatusServiceImpl;
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
public class VerificationStatusServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private VerificationStatusRepository verificationStatusRepository;
    @InjectMocks
    private VerificationStatusServiceImpl verificationStatusService;

    private VerificationStatusInputDto verificationStatusInputDto;
    private User user;
    private UserOutputDto userOutputDto;
    private VerificationStatus verificationStatus;

    @BeforeEach
    public void init(){
        user = User.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .secretKey("secretKey")
                .firstName("firstName")
                .lastName("lastName")
                .status(Status.ACTIVE)
                .build();
        verificationStatusInputDto = VerificationStatusInputDto.builder()
                .profileId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .build();
        userOutputDto = UserOutputDto.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .secretKey("secretKey")
                .firstName("firstName")
                .lastName("lastName")
                .status(Status.ACTIVE)
                .build();
        verificationStatus = VerificationStatus.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .profileId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .details("details")
                .verificationStatus(StatusOfVerification.NOT_VERIFIED)
                .build();
    }

    @Test
    public void createShouldReturnVerificationStatusOutputDto(){
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(verificationStatusRepository.save(any(VerificationStatus.class))).thenReturn(Mono.just(verificationStatus));
        Mono<VerificationStatusOutputDto> result = verificationStatusService.create(verificationStatusInputDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getDetails().equals("details") &&
                            resultDto.getVerificationStatus().equals(StatusOfVerification.NOT_VERIFIED) &&
                            resultDto.getProfile().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getProfile().getSecretKey().equals("secretKey") &&
                            resultDto.getProfile().getFirstName().equals("firstName") &&
                            resultDto.getProfile().getLastName().equals("lastName") &&
                            resultDto.getProfile().getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(verificationStatusRepository).save(any(VerificationStatus.class));
    }

    @Test
    public void requestVerificationShouldReturnVerificationStatusOutputDto(){
        Mockito.when(verificationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.just(verificationStatus));
        Mockito.when(verificationStatusRepository.save(any(VerificationStatus.class))).thenReturn(Mono.just(verificationStatus));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mono<VerificationStatusOutputDto> result = verificationStatusService.requestVerification(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getDetails().equals("details") &&
                            resultDto.getVerificationStatus().equals(StatusOfVerification.VERIFICATION_REQUESTED) &&
                            resultDto.getProfile().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getProfile().getSecretKey().equals("secretKey") &&
                            resultDto.getProfile().getFirstName().equals("firstName") &&
                            resultDto.getProfile().getLastName().equals("lastName") &&
                            resultDto.getProfile().getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(verificationStatusRepository).findById(any(UUID.class));
        Mockito.verify(verificationStatusRepository).save(any(VerificationStatus.class));
        Mockito.verify(userService).findById(any(UUID.class));
    }

    @Test
    public void requestVerificationShouldThrowResponseStatusExceptionIfVerificationStatusNotExists(){
        Mockito.when(verificationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<VerificationStatusOutputDto> result = verificationStatusService.requestVerification(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Verification status not exists"))
                .verify();
        Mockito.verify(verificationStatusRepository).findById(any(UUID.class));
    }

    @Test
    public void verifyShouldReturnVerificationStatusOutputDto(){
        Mockito.when(verificationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.just(verificationStatus));
        Mockito.when(verificationStatusRepository.save(any(VerificationStatus.class))).thenReturn(Mono.just(verificationStatus));
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(Mono.empty());
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mono<VerificationStatusOutputDto> result = verificationStatusService.verify(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getDetails().equals("details") &&
                            resultDto.getVerificationStatus().equals(StatusOfVerification.VERIFIED) &&
                            resultDto.getProfile().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getProfile().getSecretKey().equals("secretKey") &&
                            resultDto.getProfile().getFirstName().equals("firstName") &&
                            resultDto.getProfile().getLastName().equals("lastName") &&
                            resultDto.getProfile().getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(verificationStatusRepository).findById(any(UUID.class));
        Mockito.verify(verificationStatusRepository).save(any(VerificationStatus.class));
        Mockito.verify(userRepository).findById(any(UUID.class));
        Mockito.verify(userRepository).save(any(User.class));
        Mockito.verify(userService).findById(any(UUID.class));
    }

    @Test
    public void verifyShouldThrowResponseStatusExceptionIfVerificationStatusNotExists(){
        Mockito.when(verificationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<VerificationStatusOutputDto> result = verificationStatusService.requestVerification(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Verification status not exists"))
                .verify();
        Mockito.verify(verificationStatusRepository).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnVerificationStatusOutputDto(){
        Mockito.when(verificationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.just(verificationStatus));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mono<VerificationStatusOutputDto> result = verificationStatusService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getDetails().equals("details") &&
                            resultDto.getProfile().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getProfile().getSecretKey().equals("secretKey") &&
                            resultDto.getProfile().getFirstName().equals("firstName") &&
                            resultDto.getProfile().getLastName().equals("lastName") &&
                            resultDto.getProfile().getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(verificationStatusRepository).findById(any(UUID.class));
        Mockito.verify(userService).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldThrowResponseStatusExceptionIfVerificationStatusNotExists(){
        Mockito.when(verificationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<VerificationStatusOutputDto> result = verificationStatusService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Verification status not exists"))
                .verify();
        Mockito.verify(verificationStatusRepository).findById(any(UUID.class));
    }

}
