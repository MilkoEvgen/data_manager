package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.IndividualInputDto;
import com.milko.user_provider.dto.output.IndividualOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.Individual;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndividualsMapper {

    Individual toIndividual(IndividualInputDto individualInputDto);

    @Mapping(target = "user", ignore = true)
    IndividualOutputDto toIndividualOutputDto(Individual individual);

    default IndividualOutputDto toIndividualOutputDtoWithUser(Individual individual, UserOutputDto userOutputDto){
        IndividualOutputDto individualOutputDto = toIndividualOutputDto(individual);
        individualOutputDto.setUser(userOutputDto);
        return individualOutputDto;
    }
}
