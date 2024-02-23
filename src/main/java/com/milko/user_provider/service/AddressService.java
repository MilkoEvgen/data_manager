package com.milko.user_provider.service;

import com.milko.user_provider.dto.input.AddressInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AddressService {
    Mono<AddressOutputDto> create(AddressInputDto addressDto);
    Mono<AddressOutputDto> update(UUID id, AddressInputDto addressDto);
    Mono<AddressOutputDto> findById(UUID id);
}
