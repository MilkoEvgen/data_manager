package com.milko.data_manager.mapper;

import com.milko.data_manager.dto.input.MerchantInputDto;
import com.milko.data_manager.dto.output.MerchantOutputDto;
import com.milko.data_manager.dto.output.UserOutputDto;
import com.milko.data_manager.model.Merchant;
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
