package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.MerchantMemberInvitationInputDto;
import com.milko.user_provider.dto.output.MerchantMemberInvitationOutputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.model.MerchantMemberInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MerchantMemberInvitationMapper {

    MerchantMemberInvitation toInvitation(MerchantMemberInvitationInputDto memberInvitationDto);

    @Mapping(target = "merchant", ignore = true)
    MerchantMemberInvitationOutputDto toInvitationDto(MerchantMemberInvitation memberInvitation);

    default MerchantMemberInvitationOutputDto toInvitationDtoWithMerchant(MerchantMemberInvitation memberInvitation, MerchantOutputDto merchantOutputDto) {
        MerchantMemberInvitationOutputDto invitationDto = toInvitationDto(memberInvitation);
        invitationDto.setMerchant(merchantOutputDto);
        return invitationDto;
    }
}
