package com.milko.data_manager.service;

import com.milko.data_manager.dto.input.UpdateUserInputDto;
import com.milko.data_manager.dto.output.UserOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {
    Mono<UserOutputDto> update(UpdateUserInputDto updateUserInputDto);
    Mono<UserOutputDto> findById(UUID id);
    Mono<UUID> deleteById(UUID id);
}
