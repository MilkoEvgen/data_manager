package com.milko.data_manager.mapper;

import com.milko.data_manager.dto.input.AddressInputDto;
import com.milko.data_manager.dto.output.AddressOutputDto;
import com.milko.data_manager.dto.output.CountryOutputDto;
import com.milko.data_manager.model.Address;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toAddress(AddressInputDto addressInputDto);

    @Mapping(target = "country", ignore = true)
    AddressOutputDto toAddressOutputDto(Address address, @Context CountryOutputDto country);

    default AddressOutputDto toAddressOutputDtoWithCountry(Address address, CountryOutputDto country) {
        AddressOutputDto addressOutputDto = toAddressOutputDto(address, null);
        addressOutputDto.setCountry(country);
        return addressOutputDto;
    }
}
