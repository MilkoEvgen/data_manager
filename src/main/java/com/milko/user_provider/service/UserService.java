package com.milko.user_provider.service;

import com.milko.user_provider.dto.input.UpdateUserInputDto;
import com.milko.user_provider.dto.input.UserInputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {
    Mono<UserOutputDto> update(UpdateUserInputDto updateUserInputDto);
    Mono<UserOutputDto> findById(UUID id);
    Mono<UUID> deleteById(UUID id);
}
