package com.milko.data_manager.service;

import com.milko.data_manager.dto.input.MerchantInputDto;
import com.milko.data_manager.dto.output.MerchantOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantService {
    Mono<MerchantOutputDto> create(MerchantInputDto merchantDto);
    Mono<MerchantOutputDto> update(UUID id, MerchantInputDto merchantDto);
    Mono<MerchantOutputDto> findById(UUID id);
    Mono<UUID> deleteById(UUID id);
}
