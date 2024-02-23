package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.IndividualsInputDto;
import com.milko.user_provider.dto.output.IndividualsOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.Individuals;

public class IndividualsMapper {

    public static Individuals map(IndividualsInputDto individualsInputDto){
        return Individuals.builder()
                .id(individualsInputDto.getId())
                .userId(individualsInputDto.getUserId())
                .created(individualsInputDto.getCreated())
                .updated(individualsInputDto.getUpdated())
                .passportNumber(individualsInputDto.getPassportNumber())
                .phoneNumber(individualsInputDto.getPhoneNumber())
                .email(individualsInputDto.getEmail())
                .verifiedAt(individualsInputDto.getVerifiedAt())
                .archivedAt(individualsInputDto.getArchivedAt())
                .status(individualsInputDto.getStatus())
                .build();
    }

    public static IndividualsOutputDto map(Individuals individuals, UserOutputDto user){
        return IndividualsOutputDto.builder()
                .id(individuals.getId())
                .user(user)
                .created(individuals.getCreated())
                .updated(individuals.getUpdated())
                .passportNumber(individuals.getPassportNumber())
                .phoneNumber(individuals.getPhoneNumber())
                .email(individuals.getEmail())
                .verifiedAt(individuals.getVerifiedAt())
                .archivedAt(individuals.getArchivedAt())
                .status(individuals.getStatus())
                .build();
    }
}
