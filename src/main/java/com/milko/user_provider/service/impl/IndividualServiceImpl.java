package com.milko.user_provider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milko.user_provider.dto.input.RegisterIndividualInputDto;
import com.milko.user_provider.dto.input.UpdateIndividualDto;
import com.milko.user_provider.dto.output.IndividualOutputDto;
import com.milko.user_provider.exceptions.EntityNotFoundException;
import com.milko.user_provider.exceptions.FieldsNotFilledException;
import com.milko.user_provider.mapper.IndividualsMapper;
import com.milko.user_provider.model.*;
import com.milko.user_provider.repository.IndividualRepository;
import com.milko.user_provider.repository.ProfileHistoryRepository;
import com.milko.user_provider.repository.UserRepository;
import com.milko.user_provider.service.IndividualService;
import com.milko.user_provider.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class IndividualServiceImpl implements IndividualService {
    private final IndividualRepository individualRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ProfileHistoryRepository profileHistoryRepository;
    private final ObjectMapper objectMapper;
    private final IndividualsMapper individualsMapper;

    @Override
    public Mono<IndividualOutputDto> create(RegisterIndividualInputDto registerInputDto) {
        log.info("IN IndividualsService.create(), InputDto = {}", registerInputDto);
        Individual individual = createIndividualFromRegisterDto(registerInputDto);
        User user = createUserFromRegisterDto(registerInputDto);
        return userRepository.save(user)
                .flatMap(savedUser -> {
                    individual.setUserId(savedUser.getId());
                    return individualRepository.save(individual);
                })
                .flatMap(savedIndividual -> {
                    ProfileHistory profileHistory = ProfileHistory.builder()
                            .created(LocalDateTime.now())
                            .userId(savedIndividual.getUserId())
                            .profileType(ProfileType.INDIVIDUAL)
                            .reason("REGISTER")
                            .comment("User register like individual")
                            .changedValues(getStringFromObject(savedIndividual))
                            .build();
                    return profileHistoryRepository.save(profileHistory)
                            .thenReturn(savedIndividual);
                })
                .flatMap(savedIndividual -> userService.findById(savedIndividual.getUserId())
                        .flatMap(userOutputDto -> Mono.just(individualsMapper.toIndividualOutputDtoWithUser(savedIndividual, userOutputDto))));
    }

    @Override
    public Mono<IndividualOutputDto> update(UpdateIndividualDto updateIndividualDto) {
        log.info("IN IndividualsService.update(),updateIndividualDto = {}", updateIndividualDto);
        Individual newIndividual = individualsMapper.toIndividual(updateIndividualDto.getIndividualInputDto());
        return individualRepository.findById(updateIndividualDto.getIndividualId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Individual not exists")))
                .flatMap(oldIndividual -> {
                    ProfileHistory profileHistory = ProfileHistory.builder()
                            .created(LocalDateTime.now())
                            .userId(oldIndividual.getUserId())
                            .profileType(ProfileType.INDIVIDUAL)
                            .reason(updateIndividualDto.getReason())
                            .comment(updateIndividualDto.getComment())
                            .changedValues(getStringFromObject(newIndividual))
                            .build();
                    return profileHistoryRepository.save(profileHistory)
                            .thenReturn(oldIndividual);
                })
                .flatMap(oldIndividual -> userService.findById(oldIndividual.getUserId())
                .flatMap(userOutputDto -> individualRepository.save(setNewValuesToOldIndividuals(newIndividual, oldIndividual))
                        .flatMap(updatedIndividual -> Mono.just(individualsMapper.toIndividualOutputDtoWithUser(updatedIndividual, userOutputDto)))));
    }

    @Override
    public Mono<IndividualOutputDto> findById(UUID id) {
        log.info("IN IndividualsService.findById(), id = {}", id);
        return individualRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Individual not exists")))
                .flatMap(individual -> userService.findById(individual.getUserId())
                        .flatMap(userOutputDto -> Mono.just(individualsMapper.toIndividualOutputDtoWithUser(individual, userOutputDto))));
    }

    @Override
    public Mono<UUID> deleteById(UUID id) {
        log.info("IN IndividualsService.deleteById(), id = {}", id);
        return individualRepository.updateStatusToDeletedById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Individual not exists")));
    }

    private Individual createIndividualFromRegisterDto(RegisterIndividualInputDto registerDto){
        return Individual.builder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .passportNumber(registerDto.getPassportNumber())
                .phoneNumber(registerDto.getPhoneNumber())
                .email(registerDto.getEmail())
                .status(Status.ACTIVE)
                .build();
    }

    private User createUserFromRegisterDto(RegisterIndividualInputDto registerDto){
        if (registerDto.getFirstName() == null || registerDto.getLastName() == null ||
        registerDto.getSecretKey() == null || registerDto.getAddressId() == null){
            throw new FieldsNotFilledException("All fields should be filled in");
        }
        return User.builder()
                .secretKey(registerDto.getSecretKey())
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .status(Status.ACTIVE)
                .addressId(registerDto.getAddressId())
                .build();
    }

    private String getStringFromObject(Individual individual){
        try {
            return objectMapper.writeValueAsString(individual);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Individual setNewValuesToOldIndividuals(Individual newIndividual, Individual oldIndividual){
        oldIndividual.setUpdated(LocalDateTime.now());

        if (!Objects.equals(newIndividual.getPassportNumber(), null)){
            oldIndividual.setPassportNumber(newIndividual.getPassportNumber());
        }
        if (!Objects.equals(newIndividual.getPhoneNumber(), null)){
            oldIndividual.setPhoneNumber(newIndividual.getPhoneNumber());
        }
        if (!Objects.equals(newIndividual.getEmail(), null)){
            oldIndividual.setEmail(newIndividual.getEmail());
        }
        if (!Objects.equals(newIndividual.getStatus(), null)){
            oldIndividual.setStatus(newIndividual.getStatus());
        }
        return oldIndividual;
    }
}
