package com.milko.data_manager.service;

import com.milko.data_manager.dto.input.RegisterMerchantMemberInputDto;
import com.milko.data_manager.dto.input.UpdateMerchantMemberDto;
import com.milko.data_manager.dto.output.MerchantMemberOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantMemberService {
    Mono<MerchantMemberOutputDto> create(RegisterMerchantMemberInputDto registerDto);
    Mono<MerchantMemberOutputDto> update(UpdateMerchantMemberDto updateMerchantMemberDto);
    Mono<MerchantMemberOutputDto> findById(UUID id);
    Mono<UUID> deleteById(UUID id);
}
