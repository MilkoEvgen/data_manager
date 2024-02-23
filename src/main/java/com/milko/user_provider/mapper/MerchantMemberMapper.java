package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.MerchantMemberInputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.dto.output.MerchantMemberOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.MerchantMember;

public class MerchantMemberMapper {

    public static MerchantMember map(MerchantMemberInputDto memberDto){
        return MerchantMember.builder()
                .id(memberDto.getId())
                .userId(memberDto.getUserId())
                .created(memberDto.getCreated())
                .updated(memberDto.getUpdated())
                .merchantId(memberDto.getMerchantId())
                .memberRole(memberDto.getMemberRole())
                .status(memberDto.getStatus())
                .build();
    }

    public static MerchantMemberOutputDto map(MerchantMember member, UserOutputDto userOutputDto, MerchantOutputDto merchantOutputDto){
        return MerchantMemberOutputDto.builder()
                .id(member.getId())
                .user(userOutputDto)
                .created(member.getCreated())
                .updated(member.getUpdated())
                .merchant(merchantOutputDto)
                .memberRole(member.getMemberRole())
                .status(member.getStatus())
                .build();
    }
}
