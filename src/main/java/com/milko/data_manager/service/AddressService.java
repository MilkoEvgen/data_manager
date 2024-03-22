package com.milko.data_manager.service;

import com.milko.data_manager.dto.input.AddressInputDto;
import com.milko.data_manager.dto.output.AddressOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AddressService {
    Mono<AddressOutputDto> create(AddressInputDto addressDto);
    Mono<AddressOutputDto> update(UUID id, AddressInputDto addressDto);
    Mono<AddressOutputDto> findById(UUID id);
}
