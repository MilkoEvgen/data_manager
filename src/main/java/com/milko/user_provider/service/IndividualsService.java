package com.milko.user_provider.service;

import com.milko.user_provider.dto.input.IndividualsInputDto;
import com.milko.user_provider.dto.output.IndividualsOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IndividualsService {
    Mono<IndividualsOutputDto> create(IndividualsInputDto individualsDto);
    Mono<IndividualsOutputDto> update(UUID id, IndividualsInputDto individualsDto, String reason, String comment);
    Mono<IndividualsOutputDto> findById(UUID id);
    Mono<Boolean> deleteById(UUID id);
}
