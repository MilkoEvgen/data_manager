package com.milko.user_provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milko.user_provider.dto.input.IndividualInputDto;
import com.milko.user_provider.dto.input.RegisterIndividualInputDto;
import com.milko.user_provider.dto.input.UpdateIndividualDto;
import com.milko.user_provider.dto.output.IndividualOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.exceptions.EntityNotFoundException;
import com.milko.user_provider.mapper.IndividualsMapper;
import com.milko.user_provider.model.Individual;
import com.milko.user_provider.model.ProfileHistory;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.model.User;
import com.milko.user_provider.repository.IndividualRepository;
import com.milko.user_provider.repository.ProfileHistoryRepository;
import com.milko.user_provider.repository.UserRepository;
import com.milko.user_provider.service.UserService;
import com.milko.user_provider.service.impl.IndividualServiceImpl;
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
public class IndividualServiceImplTest {
    @Mock
    private IndividualRepository individualRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private ProfileHistoryRepository profileHistoryRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private IndividualsMapper individualsMapper;

    @InjectMocks
    private IndividualServiceImpl individualsService;

    private RegisterIndividualInputDto registerInputDto;
    private UpdateIndividualDto updateIndividualDto;
    private IndividualInputDto individualInputDto;
    private UserOutputDto userOutputDto;
    private User user;
    private Individual individual;
    private IndividualOutputDto individualOutputDto;

    @BeforeEach
    public void init(){
        registerInputDto = RegisterIndividualInputDto.builder()
                .authServiceId(UUID.randomUUID())
                .firstName("firstName")
                .lastName("lastName")
                .addressId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .build();
        individualInputDto = IndividualInputDto.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .build();
        updateIndividualDto = UpdateIndividualDto.builder()
                .individualId(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .individualInputDto(individualInputDto)
                .reason("reason")
                .comment("comment")
                .build();
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
        individual = Individual.builder()
                .id(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .passportNumber("passportNumber")
                .phoneNumber("phoneNumber")
                .email("email")
                .userId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .status(Status.ACTIVE)
                .build();
        individualOutputDto = IndividualOutputDto.builder()
                .id(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .passportNumber("passportNumber")
                .phoneNumber("phoneNumber")
                .email("email")
                .user(userOutputDto)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    public void createShouldReturnIndividualsOutputDto(){
        Mockito.when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        Mockito.when(individualRepository.save(any(Individual.class))).thenReturn(Mono.just(individual));
        Mockito.when(profileHistoryRepository.save(any(ProfileHistory.class))).thenReturn(Mono.empty());
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(individualsMapper.toIndividualOutputDtoWithUser(any(Individual.class), any(UserOutputDto.class))).thenReturn(individualOutputDto);
        Mono<IndividualOutputDto> result = individualsService.create(registerInputDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getUser().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getUser().getFirstName().equals("firstName") &&
                            resultDto.getUser().getLastName().equals("lastName") &&
                            resultDto.getUser().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getPassportNumber().equals("passportNumber") &&
                            resultDto.getPhoneNumber().equals("phoneNumber") &&
                            resultDto.getEmail().equals("email") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(userRepository).save(any(User.class));
        Mockito.verify(individualRepository).save(any(Individual.class));
        Mockito.verify(profileHistoryRepository).save(any(ProfileHistory.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(individualsMapper).toIndividualOutputDtoWithUser(any(Individual.class), any(UserOutputDto.class));
    }

    @Test
    public void updateShouldReturnIndividualsOutputDto(){
        Mockito.when(individualsMapper.toIndividual(any(IndividualInputDto.class))).thenReturn(individual);
        Mockito.when(individualRepository.findById(any(UUID.class))).thenReturn(Mono.just(individual));
        Mockito.when(profileHistoryRepository.save(any(ProfileHistory.class))).thenReturn(Mono.empty());
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(individualRepository.save(any(Individual.class))).thenReturn(Mono.just(individual));
        Mockito.when(individualsMapper.toIndividualOutputDtoWithUser(any(Individual.class), any(UserOutputDto.class))).thenReturn(individualOutputDto);
        Mono<IndividualOutputDto> result = individualsService.update(updateIndividualDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getUser().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getUser().getFirstName().equals("firstName") &&
                            resultDto.getUser().getLastName().equals("lastName") &&
                            resultDto.getUser().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getPassportNumber().equals("passportNumber") &&
                            resultDto.getPhoneNumber().equals("phoneNumber") &&
                            resultDto.getEmail().equals("email") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(individualsMapper).toIndividual(any(IndividualInputDto.class));
        Mockito.verify(individualRepository).findById(any(UUID.class));
        Mockito.verify(profileHistoryRepository).save(any(ProfileHistory.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(individualRepository).save(any(Individual.class));
        Mockito.verify(individualsMapper).toIndividualOutputDtoWithUser(any(Individual.class), any(UserOutputDto.class));
    }

    @Test
    public void updateShouldThrowExceptionIfIndividualNotExists(){
        Mockito.when(individualsMapper.toIndividual(any(IndividualInputDto.class))).thenReturn(individual);
        Mockito.when(individualRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<IndividualOutputDto> result = individualsService.update(updateIndividualDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Individual not exists"))
                .verify();
        Mockito.verify(individualsMapper).toIndividual(any(IndividualInputDto.class));
        Mockito.verify(individualRepository).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnIndividualsOutputDto(){
        Mockito.when(individualRepository.findById(any(UUID.class))).thenReturn(Mono.just(individual));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(individualsMapper.toIndividualOutputDtoWithUser(any(Individual.class), any(UserOutputDto.class))).thenReturn(individualOutputDto);
        Mono<IndividualOutputDto> result = individualsService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getUser().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getUser().getFirstName().equals("firstName") &&
                            resultDto.getUser().getLastName().equals("lastName") &&
                            resultDto.getUser().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getPassportNumber().equals("passportNumber") &&
                            resultDto.getPhoneNumber().equals("phoneNumber") &&
                            resultDto.getEmail().equals("email") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(individualRepository).findById(any(UUID.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(individualsMapper).toIndividualOutputDtoWithUser(any(Individual.class), any(UserOutputDto.class));
    }

    @Test
    public void findByIdShouldThrowExceptionIfIndividualNotExists(){
        Mockito.when(individualRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<IndividualOutputDto> result = individualsService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Individual not exists"))
                .verify();
        Mockito.verify(individualRepository).findById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldReturnUUID(){
        Mockito.when(individualRepository.updateStatusToDeletedById(any(UUID.class)))
                .thenReturn(Mono.just(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")));
        Mono<UUID> result = individualsService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(uuid -> uuid.equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")))
                .expectComplete()
                .verify();
        Mockito.verify(individualRepository).updateStatusToDeletedById(any(UUID.class));
    }


    @Test
    public void deleteByIdShouldThrowExceptionIfIndividualNotExists(){
        Mockito.when(individualRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<UUID> result = individualsService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Individual not exists"))
                .verify();
        Mockito.verify(individualRepository).updateStatusToDeletedById(any(UUID.class));
    }

}
