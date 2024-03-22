package com.milko.data_manager.service;

import com.milko.data_manager.dto.output.ProfileHistoryOutputDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProfileHistoryService {
    Flux<ProfileHistoryOutputDto> getAllHistoryByUserId(UUID id);

    Mono<ProfileHistoryOutputDto> findById(UUID id);
}
