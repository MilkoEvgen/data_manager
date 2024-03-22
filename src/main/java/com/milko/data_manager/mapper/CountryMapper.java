package com.milko.data_manager.mapper;

import com.milko.data_manager.dto.input.CountryInputDto;
import com.milko.data_manager.dto.output.CountryOutputDto;
import com.milko.data_manager.model.Country;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    Country toCountry(CountryInputDto countryInputDto);

    CountryOutputDto toCountryOutputDto(Country country);
}
