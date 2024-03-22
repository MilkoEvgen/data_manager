package com.milko.data_manager.mapper;

import com.milko.data_manager.dto.input.MerchantMemberInputDto;
import com.milko.data_manager.dto.output.MerchantOutputDto;
import com.milko.data_manager.dto.output.MerchantMemberOutputDto;
import com.milko.data_manager.dto.output.UserOutputDto;
import com.milko.data_manager.model.MerchantMember;
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
