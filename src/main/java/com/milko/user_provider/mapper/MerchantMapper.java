package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.MerchantInputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MerchantMapper {

    Merchant toMerchant(MerchantInputDto merchantInputDto);

    @Mapping(target = "creator", ignore = true)
    MerchantOutputDto toMerchantOutputDto(Merchant merchant);

    default MerchantOutputDto toMerchantOutputDtoWithCreator(Merchant merchant, UserOutputDto creator){
        MerchantOutputDto merchantOutputDto = toMerchantOutputDto(merchant);
        merchantOutputDto.setCreator(creator);
        return merchantOutputDto;
    }
}
