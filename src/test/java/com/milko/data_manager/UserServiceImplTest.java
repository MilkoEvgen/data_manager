package com.milko.data_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milko.data_manager.dto.input.UpdateUserInputDto;
import com.milko.data_manager.dto.output.AddressOutputDto;
import com.milko.data_manager.dto.output.UserOutputDto;
import com.milko.data_manager.exceptions.EntityNotFoundException;
import com.milko.data_manager.mapper.UserMapper;
import com.milko.data_manager.model.ProfileHistory;
import com.milko.data_manager.model.Status;
import com.milko.data_manager.model.User;
import com.milko.data_manager.repository.ProfileHistoryRepository;
import com.milko.data_manager.repository.UserRepository;
import com.milko.data_manager.service.AddressService;
import com.milko.data_manager.service.impl.UserServiceImpl;
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
public class UserServiceImplTest {
    @Mock
    private ProfileHistoryRepository profileHistoryRepository;
    @Mock
    private AddressService addressService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    private AddressOutputDto addressOutputDto;
    private User user;
    private UpdateUserInputDto updateUserInputDto;
    private UserOutputDto userOutputDto;

    @BeforeEach
    public void init() {
        addressOutputDto = AddressOutputDto.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .address("address")
                .state("state")
                .city("city")
                .zipCode("zip code")
                .build();
        user = User.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .addressId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .firstName("firstName")
                .lastName("lastName")
                .status(Status.ACTIVE)
                .build();
        updateUserInputDto = UpdateUserInputDto.builder()
                .userId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .user(user)
                .reason("reason")
                .comment("comment")
                .build();
        userOutputDto = UserOutputDto.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .address(addressOutputDto)
                .firstName("firstName")
                .lastName("lastName")
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    public void updateShouldReturnUserOutputDto() {
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        Mockito.when(profileHistoryRepository.save(any(ProfileHistory.class))).thenReturn(Mono.empty());
        Mockito.when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        Mockito.when(addressService.findById(any(UUID.class))).thenReturn(Mono.just(addressOutputDto));
        Mockito.when(userMapper.toUserOutputDtoWithAddress(any(User.class), any(AddressOutputDto.class))).thenReturn(userOutputDto);
        Mono<UserOutputDto> result = userService.update(updateUserInputDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getAddress().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getAddress().getAddress().equals("address") &&
                            resultDto.getAddress().getState().equals("state") &&
                            resultDto.getAddress().getCity().equals("city") &&
                            resultDto.getAddress().getZipCode().equals("zip code") &&
                            resultDto.getFirstName().equals("firstName") &&
                            resultDto.getLastName().equals("lastName") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(userRepository).findById(any(UUID.class));
        Mockito.verify(profileHistoryRepository).save(any(ProfileHistory.class));
        Mockito.verify(userRepository).save(any(User.class));
        Mockito.verify(addressService).findById(any(UUID.class));
        Mockito.verify(userMapper).toUserOutputDtoWithAddress(any(User.class), any(AddressOutputDto.class));
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionIfUserNotExists() {
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<UserOutputDto> result = userService.update(updateUserInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("User not exists"))
                .verify();
        Mockito.verify(userRepository).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnUserOutputDto() {
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        Mockito.when(addressService.findById(any(UUID.class))).thenReturn(Mono.just(addressOutputDto));
        Mockito.when(userMapper.toUserOutputDtoWithAddress(any(User.class), any(AddressOutputDto.class))).thenReturn(userOutputDto);
        Mono<UserOutputDto> result = userService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getAddress().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getAddress().getAddress().equals("address") &&
                            resultDto.getAddress().getState().equals("state") &&
                            resultDto.getAddress().getCity().equals("city") &&
                            resultDto.getAddress().getZipCode().equals("zip code") &&
                            resultDto.getFirstName().equals("firstName") &&
                            resultDto.getLastName().equals("lastName") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(userRepository).findById(any(UUID.class));
        Mockito.verify(addressService).findById(any(UUID.class));
        Mockito.verify(userMapper).toUserOutputDtoWithAddress(any(User.class), any(AddressOutputDto.class));
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionIfUserNotExists() {
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<UserOutputDto> result = userService.update(updateUserInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("User not exists"))
                .verify();
        Mockito.verify(userRepository).findById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldReturnUUID(){
        Mockito.when(userRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.just(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")));
        Mono<UUID> result = userService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(uuid -> uuid.equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")))
                .expectComplete()
                .verify();
        Mockito.verify(userRepository).updateStatusToDeletedById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldThrowEntityNotFoundExceptionIfUserNotExists(){
        Mockito.when(userRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<UUID> result = userService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("User not exists"))
                .verify();
        Mockito.verify(userRepository).updateStatusToDeletedById(any(UUID.class));
    }


}
