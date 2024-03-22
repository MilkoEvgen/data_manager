package com.milko.data_manager.service;

import com.milko.data_manager.dto.input.RegisterIndividualInputDto;
import com.milko.data_manager.dto.input.UpdateIndividualDto;
import com.milko.data_manager.dto.output.IndividualOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IndividualService {
    Mono<IndividualOutputDto> create(RegisterIndividualInputDto inputDto);
    Mono<IndividualOutputDto> update(UpdateIndividualDto updateIndividualDto);
    Mono<IndividualOutputDto> findById(UUID id);
    Mono<IndividualOutputDto> findByAuthServiceId(UUID id);
    Mono<UUID> deleteById(UUID id);
}
