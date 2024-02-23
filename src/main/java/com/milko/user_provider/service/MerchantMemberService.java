package com.milko.user_provider.service;

import com.milko.user_provider.dto.input.MerchantMemberInputDto;
import com.milko.user_provider.dto.output.MerchantMemberOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantMemberService {
    Mono<MerchantMemberOutputDto> create(MerchantMemberInputDto merchantMemberInputDto);
    Mono<MerchantMemberOutputDto> update(UUID id, MerchantMemberInputDto merchantDto, String reason, String comment);
    Mono<MerchantMemberOutputDto> findById(UUID id);
    Mono<Integer> deleteById(UUID id);
}
