package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.VerificationStatusInputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.dto.output.VerificationStatusOutputDto;
import com.milko.user_provider.model.VerificationStatus;

public class VerificationStatusMapper {

    public static VerificationStatus map(VerificationStatusInputDto verificationStatusInputDto){
        return VerificationStatus.builder()
                .id(verificationStatusInputDto.getId())
                .created(verificationStatusInputDto.getCreated())
                .updated(verificationStatusInputDto.getUpdated())
                .profileId(verificationStatusInputDto.getProfileId())
                .profileType(verificationStatusInputDto.getProfileType())
                .details(verificationStatusInputDto.getDetails())
                .verificationStatus(verificationStatusInputDto.getVerificationStatus())
                .build();
    }

    public static VerificationStatusOutputDto map(VerificationStatus verificationStatus, UserOutputDto userOutputDto){
        return VerificationStatusOutputDto.builder()
                .id(verificationStatus.getId())
                .created(verificationStatus.getCreated())
                .updated(verificationStatus.getUpdated())
                .profile(userOutputDto)
                .profileType(verificationStatus.getProfileType())
                .details(verificationStatus.getDetails())
                .verificationStatus(verificationStatus.getVerificationStatus())
                .build();
    }
}
