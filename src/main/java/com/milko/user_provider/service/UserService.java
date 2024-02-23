package com.milko.user_provider.service;

import com.milko.user_provider.dto.input.UserInputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {
    Mono<UserOutputDto> create(UserInputDto userDto);
    Mono<UserOutputDto> update(UUID userId, UserInputDto userInputDto, String reason, String comment);
    Mono<UserOutputDto> findById(UUID id);
    Mono<Integer> deleteById(UUID id);
}
