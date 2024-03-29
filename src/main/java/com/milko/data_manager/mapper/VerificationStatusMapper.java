package com.milko.data_manager.mapper;

import com.milko.data_manager.dto.input.VerificationStatusInputDto;
import com.milko.data_manager.dto.output.UserOutputDto;
import com.milko.data_manager.dto.output.VerificationStatusOutputDto;
import com.milko.data_manager.model.VerificationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VerificationStatusMapper {

    VerificationStatus toVerification(VerificationStatusInputDto verificationStatusInputDto);

    @Mapping(target = "profile", ignore = true)
    VerificationStatusOutputDto toVerificationOutputDto(VerificationStatus verificationStatus);

    default VerificationStatusOutputDto toVerificationOutputDtoWithUser(VerificationStatus verificationStatus, UserOutputDto userOutputDto){
        VerificationStatusOutputDto verificationOutputDto = toVerificationOutputDto(verificationStatus);
        verificationOutputDto.setProfile(userOutputDto);
        return verificationOutputDto;
    }
}
