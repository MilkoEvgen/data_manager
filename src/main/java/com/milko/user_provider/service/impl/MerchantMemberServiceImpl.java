package com.milko.user_provider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milko.user_provider.dto.input.RegisterMerchantMemberInputDto;
import com.milko.user_provider.dto.input.UpdateMerchantMemberDto;
import com.milko.user_provider.dto.output.MerchantMemberOutputDto;
import com.milko.user_provider.exceptions.EntityNotFoundException;
import com.milko.user_provider.exceptions.FieldsNotFilledException;
import com.milko.user_provider.mapper.MerchantMemberMapper;
import com.milko.user_provider.model.*;
import com.milko.user_provider.repository.MerchantMemberRepository;
import com.milko.user_provider.repository.ProfileHistoryRepository;
import com.milko.user_provider.repository.UserRepository;
import com.milko.user_provider.service.MerchantMemberService;
import com.milko.user_provider.service.MerchantService;
import com.milko.user_provider.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MerchantMemberServiceImpl implements MerchantMemberService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final MerchantService merchantService;
    private final ProfileHistoryRepository profileHistoryRepository;
    private final MerchantMemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private final MerchantMemberMapper memberMapper;

    @Override
    public Mono<MerchantMemberOutputDto> create(RegisterMerchantMemberInputDto registerDto) {
        log.info("IN MerchantMemberService.create(), InputDto = {}", registerDto);
        MerchantMember merchantMember = createMerchantMemberFromRegisterDto(registerDto);
        User user = createUserFromRegisterDto(registerDto);
        return userRepository.save(user)
                .flatMap(savedUser -> {
                    merchantMember.setUserId(savedUser.getId());
                    return memberRepository.save(merchantMember);
                })
                .flatMap(savedMember -> {
                    ProfileHistory profileHistory = ProfileHistory.builder()
                            .created(LocalDateTime.now())
                            .userId(savedMember.getUserId())
                            .profileType(ProfileType.MERCHANT_MEMBER)
                            .reason("REGISTER")
                            .comment("User register like merchant member")
                            .changedValues(getStringFromObject(savedMember))
                            .build();
                    return profileHistoryRepository.save(profileHistory)
                            .thenReturn(savedMember);
                })
                .flatMap(savedMember -> merchantService.findById(merchantMember.getMerchantId())
                        .flatMap(merchantOutputDto -> userService.findById(savedMember.getUserId())
                                .flatMap(userOutputDto -> Mono.just(memberMapper.toMemberOutputDtoWithUserAndMerchant(savedMember, userOutputDto, merchantOutputDto)))));
    }

    @Override
    public Mono<MerchantMemberOutputDto> update(UpdateMerchantMemberDto updateMerchantMemberDto) {
        log.info("IN MerchantMemberService.update(), updateMerchantMemberDto = {}", updateMerchantMemberDto);
        MerchantMember newMember = updateMerchantMemberDto.getMerchantMember();
        return memberRepository.findById(updateMerchantMemberDto.getMerchantMemberId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Merchant member not exists")))
                .flatMap(oldMember -> {
                    ProfileHistory profileHistory = ProfileHistory.builder()
                            .created(LocalDateTime.now())
                            .userId(oldMember.getUserId())
                            .profileType(ProfileType.MERCHANT_MEMBER)
                            .reason(updateMerchantMemberDto.getReason())
                            .comment(updateMerchantMemberDto.getComment())
                            .changedValues(getStringFromObject(newMember))
                            .build();
                    return profileHistoryRepository.save(profileHistory)
                            .thenReturn(oldMember);
                })
                .flatMap(oldMember -> memberRepository.save(setNewValuesToOldMember(newMember, oldMember)))
                .flatMap(savedMerchantMember -> userService.findById(savedMerchantMember.getUserId())
                        .flatMap(userOutputDto -> merchantService.findById(savedMerchantMember.getMerchantId())
                        .flatMap(merchantOutputDto -> Mono.just(memberMapper.toMemberOutputDtoWithUserAndMerchant(savedMerchantMember, userOutputDto, merchantOutputDto)))));
    }

    @Override
    public Mono<MerchantMemberOutputDto> findById(UUID id) {
        log.info("IN MerchantMemberService.findById(), id = {}", id);
        return memberRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Merchant member not exists")))
                .flatMap(merchantMember -> userService.findById(merchantMember.getUserId())
                        .flatMap(userOutputDto -> merchantService.findById(merchantMember.getMerchantId())
                                .flatMap(merchantOutputDto -> Mono.just(memberMapper.toMemberOutputDtoWithUserAndMerchant(merchantMember, userOutputDto, merchantOutputDto)))));
    }

    @Override
    public Mono<UUID> deleteById(UUID id) {
        log.info("IN MerchantMemberService.deleteById(), id = {}", id);
        return memberRepository.updateStatusToDeletedById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Merchant member not exists")));
    }

    private MerchantMember createMerchantMemberFromRegisterDto(RegisterMerchantMemberInputDto registerDto){
        return MerchantMember.builder()
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .merchantId(registerDto.getMerchantId())
                .memberRole(registerDto.getMemberRole())
                .status(Status.ACTIVE)
                .build();
    }

    private User createUserFromRegisterDto(RegisterMerchantMemberInputDto registerDto){
        if (registerDto.getFirstName() == null || registerDto.getLastName() == null ||
                registerDto.getSecretKey() == null || registerDto.getAddressId() == null){
            throw new FieldsNotFilledException("All fields should be filled in");
        }
        return User.builder()
                .secretKey(registerDto.getSecretKey())
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .verifiedAt(LocalDateTime.now())
                .archivedAt(LocalDateTime.now())
                .status(Status.ACTIVE)
                .addressId(registerDto.getAddressId())
                .build();
    }

    private String getStringFromObject(MerchantMember merchantMember){
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
