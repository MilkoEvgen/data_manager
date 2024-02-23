package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.CountryInputDto;
import com.milko.user_provider.dto.output.CountryOutputDto;
import com.milko.user_provider.model.Country;

public class CountryMapper {

    public static Country map(CountryInputDto countryInputDto){
        return Country.builder()
                .id(countryInputDto.getId())
                .created(countryInputDto.getCreated())
                .updated(countryInputDto.getUpdated())
                .name(countryInputDto.getName())
                .alpha2(countryInputDto.getAlpha2())
                .alpha3(countryInputDto.getAlpha3())
                .status(countryInputDto.getStatus())
                .build();
    }
    public static CountryOutputDto map(Country country){
        return CountryOutputDto.builder()
                .id(country.getId())
                .created(country.getCreated())
                .updated(country.getUpdated())
                .name(country.getName())
                .alpha2(country.getAlpha2())
                .alpha3(country.getAlpha3())
                .status(country.getStatus())
                .build();
    }
}
