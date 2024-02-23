package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.AddressInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.dto.output.CountryOutputDto;
import com.milko.user_provider.model.Address;

public class AddressMapper {

    public static Address map(AddressInputDto addressInputDto){
        return Address.builder()
                .id(addressInputDto.getId())
                .created(addressInputDto.getCreated())
                .updated(addressInputDto.getUpdated())
                .countryId(addressInputDto.getCountryId())
                .zipCode(addressInputDto.getZipCode())
                .address(addressInputDto.getAddress())
                .archived(addressInputDto.getArchived())
                .city(addressInputDto.getCity())
                .state(addressInputDto.getState())
                .build();
    }
    public static AddressOutputDto map(Address address, CountryOutputDto country){
        return AddressOutputDto.builder()
                .id(address.getId())
                .created(address.getCreated())
                .updated(address.getUpdated())
                .country(country)
                .zipCode(address.getZipCode())
                .address(address.getAddress())
                .archived(address.getArchived())
                .city(address.getCity())
                .state(address.getState())
                .build();
    }
}
