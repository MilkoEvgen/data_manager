package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.AddressInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.dto.output.CountryOutputDto;
import com.milko.user_provider.model.Address;
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
