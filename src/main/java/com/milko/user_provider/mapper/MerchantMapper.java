package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.MerchantInputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.Merchant;

public class MerchantMapper {

    public static Merchant map(MerchantInputDto merchantInputDto){
        return Merchant.builder()
                .id(merchantInputDto.getId())
                .creatorId(merchantInputDto.getCreatorId())
                .created(merchantInputDto.getCreated())
                .updated(merchantInputDto.getUpdated())
                .companyName(merchantInputDto.getCompanyName())
                .companyId(merchantInputDto.getCompanyId())
                .email(merchantInputDto.getEmail())
                .phoneNumber(merchantInputDto.getPhoneNumber())
                .verifiedAt(merchantInputDto.getVerifiedAt())
                .archivedAt(merchantInputDto.getArchivedAt())
                .status(merchantInputDto.getStatus())
                .filled(merchantInputDto.getFilled())
                .build();
    }

    public static MerchantOutputDto map(Merchant merchant, UserOutputDto creator){
        return MerchantOutputDto.builder()
                .id(merchant.getId())
                .creator(creator)
                .created(merchant.getCreated())
                .updated(merchant.getUpdated())
                .companyName(merchant.getCompanyName())
                .companyId(merchant.getCompanyId())
                .email(merchant.getEmail())
                .phoneNumber(merchant.getPhoneNumber())
                .verifiedAt(merchant.getVerifiedAt())
                .archivedAt(merchant.getArchivedAt())
                .status(merchant.getStatus())
                .filled(merchant.getFilled())
                .build();
    }
}
