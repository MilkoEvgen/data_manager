package com.milko.data_manager;

import com.milko.data_manager.dto.input.VerificationStatusInputDto;
import com.milko.data_manager.dto.output.UserOutputDto;
import com.milko.data_manager.dto.output.VerificationStatusOutputDto;
import com.milko.data_manager.exceptions.EntityNotFoundException;
import com.milko.data_manager.mapper.VerificationStatusMapper;
import com.milko.data_manager.model.Status;
import com.milko.data_manager.model.StatusOfVerification;
import com.milko.data_manager.model.User;
import com.milko.data_manager.model.VerificationStatus;
import com.milko.data_manager.repository.UserRepository;
import com.milko.data_manager.repository.VerificationStatusRepository;
import com.milko.data_manager.service.UserService;
import com.milko.data_manager.service.impl.VerificationStatusServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
    @Mock
    private VerificationStatusMapper verificationMapper;

    @InjectMocks
    private VerificationStatusServiceImpl verificationStatusService;

    private VerificationStatusInputDto verificationStatusInputDto;
    private User user;
    private UserOutputDto userOutputDto;
    private VerificationStatus verificationStatus;
    private VerificationStatusOutputDto verificationOutputDto;

    @BeforeEach
    public void init(){
        user = User.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .firstName("firstName")
                .lastName("lastName")
                .status(Status.ACTIVE)
                .build();
        verificationStatusInputDto = VerificationStatusInputDto.builder()
                .profileId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .build();
        userOutputDto = UserOutputDto.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
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
        verificationOutputDto = VerificationStatusOutputDto.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .profile(userOutputDto)
                .profileType("USER")
                .details("details")
                .verificationStatus(StatusOfVerification.NOT_VERIFIED)
                .build();
    }

    @Test
    public void createShouldReturnVerificationStatusOutputDto(){
        Mockito.when(verificationMapper.toVerification(any(VerificationStatusInputDto.class))).thenReturn(verificationStatus);
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(verificationStatusRepository.save(any(VerificationStatus.class))).thenReturn(Mono.just(verificationStatus));
        Mockito.when(verificationMapper.toVerificationOutputDtoWithUser(any(VerificationStatus.class), any(UserOutputDto.class))).thenReturn(verificationOutputDto);
        Mono<VerificationStatusOutputDto> result = verificationStatusService.create(verificationStatusInputDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getDetails().equals("details") &&
                            resultDto.getVerificationStatus().equals(StatusOfVerification.NOT_VERIFIED) &&
                            resultDto.getProfile().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getProfile().getFirstName().equals("firstName") &&
                            resultDto.getProfile().getLastName().equals("lastName") &&
                            resultDto.getProfile().getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(verificationMapper).toVerification(any(VerificationStatusInputDto.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(verificationStatusRepository).save(any(VerificationStatus.class));
        Mockito.verify(verificationMapper).toVerificationOutputDtoWithUser(any(VerificationStatus.class), any(UserOutputDto.class));
    }

    @Test
    public void requestVerificationShouldReturnVerificationStatusOutputDto(){
        verificationOutputDto.setVerificationStatus(StatusOfVerification.VERIFICATION_REQUESTED);
        Mockito.when(verificationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.just(verificationStatus));
        Mockito.when(verificationStatusRepository.save(any(VerificationStatus.class))).thenReturn(Mono.just(verificationStatus));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(verificationMapper.toVerificationOutputDtoWithUser(any(VerificationStatus.class), any(UserOutputDto.class))).thenReturn(verificationOutputDto);
        Mono<VerificationStatusOutputDto> result = verificationStatusService.requestVerification(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getDetails().equals("details") &&
                            resultDto.getVerificationStatus().equals(StatusOfVerification.VERIFICATION_REQUESTED) &&
                            resultDto.getProfile().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getProfile().getFirstName().equals("firstName") &&
                            resultDto.getProfile().getLastName().equals("lastName") &&
                            resultDto.getProfile().getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(verificationStatusRepository).findById(any(UUID.class));
        Mockito.verify(verificationStatusRepository).save(any(VerificationStatus.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(verificationMapper).toVerificationOutputDtoWithUser(any(VerificationStatus.class), any(UserOutputDto.class));
    }

    @Test
    public void requestVerificationShouldThrowEntityNotFoundExceptionIfVerificationStatusNotExists(){
        Mockito.when(verificationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<VerificationStatusOutputDto> result = verificationStatusService.requestVerification(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Verification status not exists"))
                .verify();
        Mockito.verify(verificationStatusRepository).findById(any(UUID.class));
    }

    @Test
    public void verifyShouldReturnVerificationStatusOutputDto(){
        verificationOutputDto.setVerificationStatus(StatusOfVerification.VERIFIED);
        Mockito.when(verificationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.just(verificationStatus));
        Mockito.when(verificationStatusRepository.save(any(VerificationStatus.class))).thenReturn(Mono.just(verificationStatus));
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(Mono.empty());
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(verificationMapper.toVerificationOutputDtoWithUser(any(VerificationStatus.class), any(UserOutputDto.class))).thenReturn(verificationOutputDto);
        Mono<VerificationStatusOutputDto> result = verificationStatusService.verify(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getDetails().equals("details") &&
                            resultDto.getVerificationStatus().equals(StatusOfVerification.VERIFIED) &&
                            resultDto.getProfile().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
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
        Mockito.verify(verificationMapper).toVerificationOutputDtoWithUser(any(VerificationStatus.class), any(UserOutputDto.class));
    }

    @Test
    public void verifyShouldThrowEntityNotFoundExceptionIfVerificationStatusNotExists(){
        Mockito.when(verificationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<VerificationStatusOutputDto> result = verificationStatusService.requestVerification(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Verification status not exists"))
                .verify();
        Mockito.verify(verificationStatusRepository).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnVerificationStatusOutputDto(){
        Mockito.when(verificationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.just(verificationStatus));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(verificationMapper.toVerificationOutputDtoWithUser(any(VerificationStatus.class), any(UserOutputDto.class))).thenReturn(verificationOutputDto);
        Mono<VerificationStatusOutputDto> result = verificationStatusService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getDetails().equals("details") &&
                            resultDto.getProfile().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getProfile().getFirstName().equals("firstName") &&
                            resultDto.getProfile().getLastName().equals("lastName") &&
                            resultDto.getProfile().getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(verificationStatusRepository).findById(any(UUID.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(verificationMapper).toVerificationOutputDtoWithUser(any(VerificationStatus.class), any(UserOutputDto.class));
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionIfVerificationStatusNotExists(){
        Mockito.when(verificationStatusRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<VerificationStatusOutputDto> result = verificationStatusService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Verification status not exists"))
                .verify();
        Mockito.verify(verificationStatusRepository).findById(any(UUID.class));
    }

}
