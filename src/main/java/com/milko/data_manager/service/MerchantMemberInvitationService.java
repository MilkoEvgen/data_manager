package com.milko.data_manager.service;

import com.milko.data_manager.dto.input.MerchantMemberInvitationInputDto;
import com.milko.data_manager.dto.output.MerchantMemberInvitationOutputDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantMemberInvitationService {

    Mono<MerchantMemberInvitationOutputDto> create(MerchantMemberInvitationInputDto inputDto);

    Flux<MerchantMemberInvitationOutputDto> findAllByMerchantId(UUID merchantId);

    Mono<UUID> deleteById(UUID id);
}
