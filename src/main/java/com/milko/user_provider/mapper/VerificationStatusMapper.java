package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.VerificationStatusInputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.dto.output.VerificationStatusOutputDto;
import com.milko.user_provider.model.VerificationStatus;
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
