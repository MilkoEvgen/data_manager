package com.milko.user_provider.service;

import com.milko.user_provider.dto.input.ProfileHistoryInputDto;
import com.milko.user_provider.dto.output.ProfileHistoryOutputDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProfileHistoryService {
    Flux<ProfileHistoryOutputDto> getAllHistoryByUserId(UUID id);

    Mono<ProfileHistoryOutputDto> findById(UUID id);
}
