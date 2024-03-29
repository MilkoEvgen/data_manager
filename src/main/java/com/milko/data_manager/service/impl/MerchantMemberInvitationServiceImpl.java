package com.milko.data_manager.service.impl;

import com.milko.data_manager.dto.input.MerchantMemberInvitationInputDto;
import com.milko.data_manager.dto.output.MerchantMemberInvitationOutputDto;
import com.milko.data_manager.exceptions.EntityNotFoundException;
import com.milko.data_manager.mapper.MerchantMemberInvitationMapper;
import com.milko.data_manager.model.MerchantMemberInvitation;
import com.milko.data_manager.model.Status;
import com.milko.data_manager.repository.MerchantMemberInvitationRepository;
import com.milko.data_manager.service.MerchantMemberInvitationService;
import com.milko.data_manager.service.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MerchantMemberInvitationServiceImpl implements MerchantMemberInvitationService {
    private final MerchantMemberInvitationRepository invitationRepository;
    private final MerchantService merchantService;
    private final MerchantMemberInvitationMapper invitationMapper;

    @Override
    public Mono<MerchantMemberInvitationOutputDto> create(MerchantMemberInvitationInputDto inputDto) {
        log.info("IN MerchantMemberInvitationService.create(), inputDto = {}", inputDto);
        MerchantMemberInvitation memberInvitation = invitationMapper.toInvitation(inputDto);
        memberInvitation.setCreated(LocalDateTime.now());
        memberInvitation.setExpires(LocalDateTime.now().plusDays(inputDto.getValidForDays()));
        memberInvitation.setStatus(Status.ACTIVE);
        return merchantService.findById(memberInvitation.getMerchantId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Merchant not exists")))
                .flatMap(merchantOutputDto -> invitationRepository.save(memberInvitation)
                        .flatMap(savedInvitation -> Mono.just(invitationMapper.toInvitationDtoWithMerchant(savedInvitation, merchantOutputDto))));
    }

    @Override
    public Flux<MerchantMemberInvitationOutputDto> findAllByMerchantId(UUID merchantId) {
        log.info("IN MerchantMemberInvitationService.findAllByMerchantId(), id = {}", merchantId);
        return invitationRepository.findAllByMerchantId(merchantId)
                .flatMap(merchantMemberInvitation -> Mono.just(invitationMapper.toInvitationDtoWithMerchant(merchantMemberInvitation, null)));
    }

    @Override
    public Mono<UUID> deleteById(UUID id) {
        log.info("IN MerchantMemberInvitationService.deleteById(), id = {}", id);
        return invitationRepository.updateStatusToDeletedById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Merchant member invitation not exists")));
    }
}
