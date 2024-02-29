package com.milko.user_provider.service;

import com.milko.user_provider.dto.input.MerchantMemberInputDto;
import com.milko.user_provider.dto.input.RegisterMerchantMemberInputDto;
import com.milko.user_provider.dto.input.UpdateMerchantMemberDto;
import com.milko.user_provider.dto.output.MerchantMemberOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantMemberService {
    Mono<MerchantMemberOutputDto> create(RegisterMerchantMemberInputDto registerDto);
    Mono<MerchantMemberOutputDto> update(UpdateMerchantMemberDto updateMerchantMemberDto);
    Mono<MerchantMemberOutputDto> findById(UUID id);
    Mono<UUID> deleteById(UUID id);
}
