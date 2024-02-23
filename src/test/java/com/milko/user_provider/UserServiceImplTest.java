package com.milko.user_provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milko.user_provider.dto.input.ProfileHistoryInputDto;
import com.milko.user_provider.dto.input.UserInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.model.User;
import com.milko.user_provider.repository.UserRepository;
import com.milko.user_provider.service.AddressService;
import com.milko.user_provider.service.ProfileHistoryService;
import com.milko.user_provider.service.impl.UserServiceImpl;
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
public class UserServiceImplTest {
    @Mock
    private ProfileHistoryService profileHistoryService;
    @Mock
    private AddressService addressService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private UserServiceImpl userService;

    private UserInputDto userInputDto;
    private AddressOutputDto addressOutputDto;
    private User user;

    @BeforeEach
    public void init() {
        userInputDto = UserInputDto.builder()
                .addressId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .build();
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
                .secretKey("secretKey")
                .firstName("firstName")
                .lastName("lastName")
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    public void createShouldReturnUserOutputDto() {
        Mockito.when(addressService.findById(any(UUID.class))).thenReturn(Mono.just(addressOutputDto));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        Mono<UserOutputDto> result = userService.create(userInputDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getAddress().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getAddress().getAddress().equals("address") &&
                            resultDto.getAddress().getState().equals("state") &&
                            resultDto.getAddress().getCity().equals("city") &&
                            resultDto.getAddress().getZipCode().equals("zip code") &&
                            resultDto.getSecretKey().equals("secretKey") &&
                            resultDto.getFirstName().equals("firstName") &&
                            resultDto.getLastName().equals("lastName") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(addressService).findById(any(UUID.class));
        Mockito.verify(userRepository).save(any(User.class));
    }

    @Test
    public void createShouldThrowResponseStatusExceptionIfAddressNotExists() {
        Mockito.when(addressService.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<UserOutputDto> result = userService.create(userInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Address not exists"))
                .verify();
        Mockito.verify(addressService).findById(any(UUID.class));
    }

    @Test
    public void updateShouldReturnUserOutputDto() {
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        Mockito.when(profileHistoryService.create(any(ProfileHistoryInputDto.class))).thenReturn(Mono.empty());
        Mockito.when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        Mockito.when(addressService.findById(any(UUID.class))).thenReturn(Mono.just(addressOutputDto));
        Mono<UserOutputDto> result = userService.update(UUID.randomUUID(), userInputDto, "reason", "comment");
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getAddress().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getAddress().getAddress().equals("address") &&
                            resultDto.getAddress().getState().equals("state") &&
                            resultDto.getAddress().getCity().equals("city") &&
                            resultDto.getAddress().getZipCode().equals("zip code") &&
                            resultDto.getSecretKey().equals("secretKey") &&
                            resultDto.getFirstName().equals("firstName") &&
                            resultDto.getLastName().equals("lastName") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(userRepository).findById(any(UUID.class));
        Mockito.verify(profileHistoryService).create(any(ProfileHistoryInputDto.class));
        Mockito.verify(userRepository).save(any(User.class));
        Mockito.verify(addressService).findById(any(UUID.class));
    }

    @Test
    public void updateShouldThrowResponseStatusExceptionIfUserNotExists() {
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<UserOutputDto> result = userService.update(UUID.randomUUID(), userInputDto, "reason", "comment");
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("User not exists"))
                .verify();
        Mockito.verify(userRepository).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnUserOutputDto() {
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.just(user));
        Mockito.when(addressService.findById(any(UUID.class))).thenReturn(Mono.just(addressOutputDto));
        Mono<UserOutputDto> result = userService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getAddress().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getAddress().getAddress().equals("address") &&
                            resultDto.getAddress().getState().equals("state") &&
                            resultDto.getAddress().getCity().equals("city") &&
                            resultDto.getAddress().getZipCode().equals("zip code") &&
                            resultDto.getSecretKey().equals("secretKey") &&
                            resultDto.getFirstName().equals("firstName") &&
                            resultDto.getLastName().equals("lastName") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(userRepository).findById(any(UUID.class));
        Mockito.verify(addressService).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldThrowResponseStatusExceptionIfUserNotExists() {
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<UserOutputDto> result = userService.update(UUID.randomUUID(), userInputDto, "reason", "comment");
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("User not exists"))
                .verify();
        Mockito.verify(userRepository).findById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldReturnInteger(){
        Mockito.when(userRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.just(1));
        Mono<Integer> result = userService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(integer -> integer == 1)
                .expectComplete()
                .verify();
        Mockito.verify(userRepository).updateStatusToDeletedById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldThrowResponseStatusExceptionIfUserNotExists(){
        Mockito.when(userRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.just(0));
        Mono<Integer> result = userService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("User not exists"))
                .verify();
        Mockito.verify(userRepository).updateStatusToDeletedById(any(UUID.class));
    }


}
