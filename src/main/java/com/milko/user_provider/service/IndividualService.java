package com.milko.user_provider.service;

import com.milko.user_provider.dto.input.RegisterIndividualInputDto;
import com.milko.user_provider.dto.input.UpdateIndividualDto;
import com.milko.user_provider.dto.output.IndividualOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IndividualService {
    Mono<IndividualOutputDto> create(RegisterIndividualInputDto inputDto);
    Mono<IndividualOutputDto> update(UpdateIndividualDto updateIndividualDto);
    Mono<IndividualOutputDto> findById(UUID id);
    Mono<IndividualOutputDto> findByAuthServiceId(UUID id);
    Mono<UUID> deleteById(UUID id);
}
