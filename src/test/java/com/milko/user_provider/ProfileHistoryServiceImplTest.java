package com.milko.user_provider;

import com.milko.user_provider.dto.output.ProfileHistoryOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.exceptions.EntityNotFoundException;
import com.milko.user_provider.mapper.ProfileHistoryMapper;
import com.milko.user_provider.mapper.UserMapper;
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
    @Mock
    private UserMapper userMapper;
    @Mock
    private ProfileHistoryMapper profileHistoryMapper;
    @InjectMocks
    private ProfileHistoryServiceImpl profileHistoryService;

    private User user;
    private ProfileHistory profileHistory;
    private UserOutputDto userOutputDto;
    private ProfileHistoryOutputDto profileHistoryOutputDto;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .firstName("firstName")
                .lastName("lastName")
                .status(Status.ACTIVE)
                .build();
        userOutputDto = UserOutputDto.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .firstName("firstName")
                .lastName("lastName")
                .status(Status.ACTIVE)
                .build();
        profileHistory = ProfileHistory.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .userId(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .reason("reason")
                .comment("comment")
                .changedValues("changed values")
                .build();
        profileHistoryOutputDto = ProfileHistoryOutputDto.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .user(userOutputDto)
                .reason("reason")
                .comment("comment")
                .changedValues("changed values")
                .build();
    }

    @Test
    public void getAllHistoryByUserIdShouldReturnFluxOfProfileHistoryOutputDto() {
        Mockito.when(profileHistoryRepository.getAllByUserId(any(UUID.class))).thenReturn(Flux.just(profileHistory));
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        Mockito.when(userMapper.toUserOutputDto(any(User.class))).thenReturn(userOutputDto);
        Mockito.when(profileHistoryMapper.toProfileHistoryWithUser(any(ProfileHistory.class), any(UserOutputDto.class))).thenReturn(profileHistoryOutputDto);
        Flux<ProfileHistoryOutputDto> result = profileHistoryService.getAllHistoryByUserId(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(profileHistoryOutputDto -> {
                    return profileHistoryOutputDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            profileHistoryOutputDto.getUser().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            profileHistoryOutputDto.getUser().getFirstName().equals("firstName") &&
                            profileHistoryOutputDto.getUser().getLastName().equals("lastName") &&
                            profileHistoryOutputDto.getUser().getStatus().equals(Status.ACTIVE) &&
                            profileHistoryOutputDto.getReason().equals("reason") &&
                            profileHistoryOutputDto.getComment().equals("comment") &&
                            profileHistoryOutputDto.getChangedValues().equals("changed values");
                })
                .expectComplete()
                .verify();
        Mockito.verify(profileHistoryRepository).getAllByUserId(any(UUID.class));
        Mockito.verify(userRepository).findById(any(UUID.class));
        Mockito.verify(userMapper).toUserOutputDto(any(User.class));
        Mockito.verify(profileHistoryMapper).toProfileHistoryWithUser(any(ProfileHistory.class), any(UserOutputDto.class));
    }

    @Test
    public void getAllHistoryByUserIdShouldReturnEmptyFlux() {
        Mockito.when(profileHistoryRepository.getAllByUserId(any(UUID.class))).thenReturn(Flux.empty());
        Flux<ProfileHistoryOutputDto> result = profileHistoryService.getAllHistoryByUserId(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextCount(0)
                .expectComplete()
                .verify();
        Mockito.verify(profileHistoryRepository).getAllByUserId(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnProfileHistoryOutputDto() {
        Mockito.when(profileHistoryRepository.findById(any(UUID.class))).thenReturn(Mono.just(profileHistory));
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        Mockito.when(userMapper.toUserOutputDto(any(User.class))).thenReturn(userOutputDto);
        Mockito.when(profileHistoryMapper.toProfileHistoryWithUser(any(ProfileHistory.class), any(UserOutputDto.class))).thenReturn(profileHistoryOutputDto);
        Mono<ProfileHistoryOutputDto> result = profileHistoryService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(profileHistoryOutputDto -> {
                    return profileHistoryOutputDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            profileHistoryOutputDto.getUser().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            profileHistoryOutputDto.getUser().getFirstName().equals("firstName") &&
                            profileHistoryOutputDto.getUser().getLastName().equals("lastName") &&
                            profileHistoryOutputDto.getUser().getStatus().equals(Status.ACTIVE) &&
                            profileHistoryOutputDto.getReason().equals("reason") &&
                            profileHistoryOutputDto.getComment().equals("comment") &&
                            profileHistoryOutputDto.getChangedValues().equals("changed values");
                })
                .expectComplete()
                .verify();
        Mockito.verify(profileHistoryRepository).findById(any(UUID.class));
        Mockito.verify(userRepository).findById(any(UUID.class));
        Mockito.verify(userMapper).toUserOutputDto(any(User.class));
        Mockito.verify(profileHistoryMapper).toProfileHistoryWithUser(any(ProfileHistory.class), any(UserOutputDto.class));
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionIfHistoryNotExists() {
        Mockito.when(profileHistoryRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<ProfileHistoryOutputDto> result = profileHistoryService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("History not exists"))
                .verify();
        Mockito.verify(profileHistoryRepository).findById(any(UUID.class));
    }




}
