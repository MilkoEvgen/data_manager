package com.milko.user_provider.service;

import com.milko.user_provider.dto.input.MerchantMemberInvitationInputDto;
import com.milko.user_provider.dto.output.MerchantMemberInvitationOutputDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantMemberInvitationService {

    Mono<MerchantMemberInvitationOutputDto> create(MerchantMemberInvitationInputDto inputDto);

    Flux<MerchantMemberInvitationOutputDto> findAllByMerchantId(UUID merchantId);

    Mono<UUID> deleteById(UUID id);
}
