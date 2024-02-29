package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.CountryInputDto;
import com.milko.user_provider.dto.output.CountryOutputDto;
import com.milko.user_provider.model.Country;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    Country toCountry(CountryInputDto countryInputDto);

    CountryOutputDto toCountryOutputDto(Country country);
}
