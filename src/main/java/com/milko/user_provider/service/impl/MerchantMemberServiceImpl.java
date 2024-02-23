package com.milko.user_provider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milko.user_provider.dto.input.MerchantMemberInputDto;
import com.milko.user_provider.dto.input.ProfileHistoryInputDto;
import com.milko.user_provider.dto.output.MerchantMemberOutputDto;
import com.milko.user_provider.mapper.MerchantMemberMapper;
import com.milko.user_provider.model.MerchantMember;
import com.milko.user_provider.model.ProfileType;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.model.User;
import com.milko.user_provider.repository.MerchantMemberRepository;
import com.milko.user_provider.service.MerchantMemberService;
import com.milko.user_provider.service.MerchantService;
import com.milko.user_provider.service.ProfileHistoryService;
import com.milko.user_provider.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MerchantMemberServiceImpl implements MerchantMemberService {
    private final UserService userService;
    private final MerchantService merchantService;
    private final ProfileHistoryService profileHistoryService;
    private final MerchantMemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<MerchantMemberOutputDto> create(MerchantMemberInputDto merchantMemberInputDto) {
        log.info("IN MerchantMemberService.create(), InputDto = {}", merchantMemberInputDto);
        MerchantMember merchantMember = MerchantMemberMapper.map(merchantMemberInputDto);
        merchantMember.setCreated(LocalDateTime.now());
        merchantMember.setUpdated(LocalDateTime.now());
        merchantMember.setStatus(Status.ACTIVE);
        return userService.findById(merchantMemberInputDto.getUserId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not exists")))
                .flatMap(userOutputDto -> merchantService.findById(merchantMemberInputDto.getMerchantId())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found")))
                        .flatMap(merchantOutputDto -> memberRepository.save(merchantMember)
                                .flatMap(savedMerchantMember -> Mono.just(MerchantMemberMapper.map(savedMerchantMember, userOutputDto, merchantOutputDto)))));
    }

    @Override
    public Mono<MerchantMemberOutputDto> update(UUID id, MerchantMemberInputDto memberDto, String reason, String comment) {
        log.info("IN MerchantMemberService.update(), id = {}, InputDto = {}, reason = {}, comment = {}", id, memberDto, reason, comment);
        MerchantMember newMember = MerchantMemberMapper.map(memberDto);
        return memberRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant member not exists")))
                .flatMap(oldMember -> {
                    ProfileHistoryInputDto historyInputDto = ProfileHistoryInputDto.builder()
                            .created(LocalDateTime.now())
                            .profileId(oldMember.getUserId())
                            .profileType(ProfileType.MERCHANT_MEMBER)
                            .reason(reason)
                            .comment(comment)
                            .changedValues(getValuesToChange(newMember))
                            .build();
                    return profileHistoryService.create(historyInputDto)
                            .thenReturn(oldMember);
                })
                .flatMap(oldMember -> memberRepository.save(setNewValuesToOldMember(newMember, oldMember)))
                .flatMap(savedMerchantMember -> userService.findById(savedMerchantMember.getUserId())
                        .flatMap(userOutputDto -> merchantService.findById(savedMerchantMember.getMerchantId())
                        .flatMap(merchantOutputDto -> Mono.just(MerchantMemberMapper.map(savedMerchantMember, userOutputDto, merchantOutputDto)))));
    }

    @Override
    public Mono<MerchantMemberOutputDto> findById(UUID id) {
        log.info("IN MerchantMemberService.findById(), id = {}", id);
        return memberRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant member not exists")))
                .flatMap(merchantMember -> userService.findById(merchantMember.getUserId())
                        .flatMap(userOutputDto -> merchantService.findById(merchantMember.getMerchantId())
                                .flatMap(merchantOutputDto -> Mono.just(MerchantMemberMapper.map(merchantMember, userOutputDto, merchantOutputDto)))));
    }

    @Override
    public Mono<Integer> deleteById(UUID id) {
        log.info("IN MerchantMemberService.deleteById(), id = {}", id);
        return memberRepository.updateStatusToDeletedById(id)
                .flatMap(integer -> {
                    if (integer == 0){
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant member not exists"));
                    }
                    return Mono.just(integer);
                });
    }

    private String getValuesToChange(MerchantMember merchantMember){
        try {
            return objectMapper.writeValueAsString(merchantMember);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private MerchantMember setNewValuesToOldMember(MerchantMember newMember, MerchantMember oldMember){
        oldMember.setUpdated(LocalDateTime.now());

        if (!Objects.equals(newMember.getMemberRole(), null)){
            oldMember.setMemberRole(newMember.getMemberRole());
        }
        if (!Objects.equals(newMember.getStatus(), null)){
            oldMember.setStatus(newMember.getStatus());
        }
        return oldMember;
    }
}
