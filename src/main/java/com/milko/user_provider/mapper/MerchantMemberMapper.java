package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.MerchantMemberInputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.dto.output.MerchantMemberOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.MerchantMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MerchantMemberMapper {

    MerchantMember toMember(MerchantMemberInputDto memberDto);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "merchant", ignore = true)
    MerchantMemberOutputDto toMemberOutputDto(MerchantMember member);

    default MerchantMemberOutputDto toMemberOutputDtoWithUserAndMerchant(MerchantMember member,
                                                                         UserOutputDto userOutputDto,
                                                                         MerchantOutputDto merchantOutputDto){
        MerchantMemberOutputDto memberOutputDto = toMemberOutputDto(member);
        memberOutputDto.setUser(userOutputDto);
        memberOutputDto.setMerchant(merchantOutputDto);
        return memberOutputDto;
    }
}
