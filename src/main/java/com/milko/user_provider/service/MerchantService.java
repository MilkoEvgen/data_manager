package com.milko.user_provider.service;

import com.milko.user_provider.dto.input.MerchantInputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantService {
    Mono<MerchantOutputDto> create(MerchantInputDto merchantDto);
    Mono<MerchantOutputDto> update(UUID id, MerchantInputDto merchantDto);
    Mono<MerchantOutputDto> findById(UUID id);
    Mono<UUID> deleteById(UUID id);
}
