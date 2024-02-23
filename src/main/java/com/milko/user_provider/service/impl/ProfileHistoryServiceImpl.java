package com.milko.user_provider.service.impl;

import com.milko.user_provider.dto.input.ProfileHistoryInputDto;
import com.milko.user_provider.dto.output.ProfileHistoryOutputDto;
import com.milko.user_provider.mapper.ProfileHistoryMapper;
import com.milko.user_provider.mapper.UserMapper;
import com.milko.user_provider.model.ProfileHistory;
import com.milko.user_provider.repository.ProfileHistoryRepository;
import com.milko.user_provider.repository.UserRepository;
import com.milko.user_provider.service.ProfileHistoryService;
import com.milko.user_provider.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileHistoryServiceImpl implements ProfileHistoryService {
    private final UserRepository userRepository;
    private final ProfileHistoryRepository profileHistoryRepository;

    @Override
    public Mono<Void> create(ProfileHistoryInputDto historyInputDto) {
        log.info("IN ProfileHistoryService.create(), InputDto = {}", historyInputDto);
        ProfileHistory profileHistory = ProfileHistoryMapper.map(historyInputDto);
        profileHistory.setCreated(LocalDateTime.now());
        return userRepository.findById(historyInputDto.getProfileId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not exists")))
                .flatMap(user -> profileHistoryRepository.save(ProfileHistoryMapper.map(historyInputDto)))
                .then(Mono.empty());
    }

    //Здесь не достаю адрес и страну, в UserMapper сетаю null
    @Override
    public Flux<ProfileHistoryOutputDto> getAllHistoryByProfileId(UUID profileId) {
        log.info("IN ProfileHistoryService.getAllHistoryByProfileId(), id = {}", profileId);
        return profileHistoryRepository.getAllByProfileId(profileId)
                .flatMap(profileHistory -> userRepository.findById(profileHistory.getProfileId())
                        .flatMap(user -> Mono.just(ProfileHistoryMapper.map(profileHistory, UserMapper.map(user, null)))));
    }

    @Override
    public Mono<ProfileHistoryOutputDto> findById(UUID id) {
        log.info("IN ProfileHistoryService.findById(), id = {}", id);
        return profileHistoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "History not exists")))
                .flatMap(profileHistory -> userRepository.findById(profileHistory.getProfileId())
                        .flatMap(user -> Mono.just(ProfileHistoryMapper.map(profileHistory, UserMapper.map(user, null)))));
    }
}
