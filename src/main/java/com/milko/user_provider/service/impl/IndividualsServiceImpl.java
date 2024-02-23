package com.milko.user_provider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.milko.user_provider.dto.input.IndividualsInputDto;
import com.milko.user_provider.dto.input.ProfileHistoryInputDto;
import com.milko.user_provider.dto.output.IndividualsOutputDto;
import com.milko.user_provider.mapper.IndividualsMapper;
import com.milko.user_provider.model.Individuals;
import com.milko.user_provider.model.ProfileType;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.model.User;
import com.milko.user_provider.repository.IndividualsRepository;
import com.milko.user_provider.service.IndividualsService;
import com.milko.user_provider.service.ProfileHistoryService;
import com.milko.user_provider.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class IndividualsServiceImpl implements IndividualsService {
    private final IndividualsRepository individualsRepository;
    private final UserService userService;
    private final ProfileHistoryService profileHistoryService;
    private final ObjectMapper objectMapper;

    //Нормально ли все поля с временем сетать сразу?
    @Override
    public Mono<IndividualsOutputDto> create(IndividualsInputDto individualsInputDto) {
        log.info("IN IndividualsService.create(), InputDto = {}", individualsInputDto);
        Individuals individuals = IndividualsMapper.map(individualsInputDto);
        individuals.setCreated(LocalDateTime.now());
        individuals.setUpdated(LocalDateTime.now());
        individuals.setVerifiedAt(LocalDateTime.now());
        individuals.setArchivedAt(LocalDateTime.now());
        individuals.setStatus(Status.ACTIVE);
        return userService.findById(individuals.getUserId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not exists")))
                .flatMap(userOutputDto -> individualsRepository.save(individuals)
                        .flatMap(savedIndividuals -> Mono.just(IndividualsMapper.map(savedIndividuals, userOutputDto))));
    }

    @Override
    public Mono<IndividualsOutputDto> update(UUID id, IndividualsInputDto individualsInputDto, String reason, String comment) {
        log.info("IN IndividualsService.update(),id = {}, InputDto = {}, reason = {}, comment = {}", id, individualsInputDto, reason, comment);
        Individuals newIndividuals = IndividualsMapper.map(individualsInputDto);
        return individualsRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual not exists")))
                .flatMap(oldIndividuals -> {
                    ProfileHistoryInputDto historyInputDto = ProfileHistoryInputDto.builder()
                            .created(LocalDateTime.now())
                            .profileId(oldIndividuals.getUserId())
                            .profileType(ProfileType.INDIVIDUAL)
                            .reason(reason)
                            .comment(comment)
                            .changedValues(getValuesToChange(newIndividuals))
                            .build();
                    return profileHistoryService.create(historyInputDto)
                            .thenReturn(oldIndividuals);
                })
                .flatMap(oldIndividuals -> userService.findById(oldIndividuals.getUserId())
                .flatMap(userOutputDto -> individualsRepository.save(setNewValuesToOldIndividuals(newIndividuals, oldIndividuals))
                        .flatMap(updatedIndividuals -> Mono.just(IndividualsMapper.map(updatedIndividuals, userOutputDto)))));
    }

    @Override
    public Mono<IndividualsOutputDto> findById(UUID id) {
        log.info("IN IndividualsService.findById(), id = {}", id);
        return individualsRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual not exists")))
                .flatMap(individuals -> userService.findById(individuals.getUserId())
                        .flatMap(userOutputDto -> Mono.just(IndividualsMapper.map(individuals, userOutputDto))));
    }

    @Override
    public Mono<Boolean> deleteById(UUID id) {
        log.info("IN IndividualsService.deleteById(), id = {}", id);
        return individualsRepository.updateStatusToDeletedById(id)
                .flatMap(result -> {
                    if (!result){
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Individual not exists"));
                    }
                    return Mono.just(true);
                });
    }

    private String getValuesToChange(Individuals individuals){
        try {
            return objectMapper.writeValueAsString(individuals);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Individuals setNewValuesToOldIndividuals(Individuals newIndividuals, Individuals oldIndividuals){
        oldIndividuals.setUpdated(LocalDateTime.now());

        if (!Objects.equals(newIndividuals.getPassportNumber(), null)){
            oldIndividuals.setPassportNumber(newIndividuals.getPassportNumber());
        }
        if (!Objects.equals(newIndividuals.getPhoneNumber(), null)){
            oldIndividuals.setPhoneNumber(newIndividuals.getPhoneNumber());
        }
        if (!Objects.equals(newIndividuals.getEmail(), null)){
            oldIndividuals.setEmail(newIndividuals.getEmail());
        }
        if (!Objects.equals(newIndividuals.getStatus(), null)){
            oldIndividuals.setStatus(newIndividuals.getStatus());
        }
        return oldIndividuals;
    }
}
