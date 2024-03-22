package com.milko.data_manager.mapper;

import com.milko.data_manager.dto.input.MerchantMemberInvitationInputDto;
import com.milko.data_manager.dto.output.MerchantMemberInvitationOutputDto;
import com.milko.data_manager.dto.output.MerchantOutputDto;
import com.milko.data_manager.model.MerchantMemberInvitation;
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
