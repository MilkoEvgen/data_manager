package com.milko.data_manager.mapper;

import com.milko.data_manager.dto.input.IndividualInputDto;
import com.milko.data_manager.dto.output.IndividualOutputDto;
import com.milko.data_manager.dto.output.UserOutputDto;
import com.milko.data_manager.model.Individual;
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
