package com.milko.data_manager.service.impl;

import com.milko.data_manager.dto.output.ProfileHistoryOutputDto;
import com.milko.data_manager.exceptions.EntityNotFoundException;
import com.milko.data_manager.mapper.ProfileHistoryMapper;
import com.milko.data_manager.mapper.UserMapper;
import com.milko.data_manager.repository.ProfileHistoryRepository;
import com.milko.data_manager.repository.UserRepository;
import com.milko.data_manager.service.ProfileHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileHistoryServiceImpl implements ProfileHistoryService {
    private final UserRepository userRepository;
    private final ProfileHistoryRepository profileHistoryRepository;
    private final UserMapper userMapper;
    private final ProfileHistoryMapper profileHistoryMapper;

    @Override
    public Flux<ProfileHistoryOutputDto> getAllHistoryByUserId(UUID userId) {
        log.info("IN ProfileHistoryService.getAllHistoryByProfileId(), id = {}", userId);
        return profileHistoryRepository.getAllByUserId(userId)
                .flatMap(profileHistory -> userRepository.findById(profileHistory.getUserId())
                        .flatMap(user -> Mono.just(profileHistoryMapper.toProfileHistoryWithUser(profileHistory, userMapper.toUserOutputDto(user)))));
    }

    @Override
    public Mono<ProfileHistoryOutputDto> findById(UUID id) {
        log.info("IN ProfileHistoryService.findById(), id = {}", id);
        return profileHistoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("History not exists")))
                .flatMap(profileHistory -> userRepository.findById(profileHistory.getUserId())
                        .flatMap(user -> Mono.just(profileHistoryMapper.toProfileHistoryWithUser(profileHistory, userMapper.toUserOutputDto(user)))));
    }
}
