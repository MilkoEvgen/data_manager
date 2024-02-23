package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.UserInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.User;

public class UserMapper {

    public static User map(UserInputDto userInputDto){
        return User.builder()
                .id(userInputDto.getId())
                .secretKey(userInputDto.getSecretKey())
                .created(userInputDto.getCreated())
                .updated(userInputDto.getUpdated())
                .firstName(userInputDto.getFirstName())
                .lastName(userInputDto.getLastName())
                .verifiedAt(userInputDto.getVerifiedAt())
                .archivedAt(userInputDto.getArchivedAt())
                .status(userInputDto.getStatus())
                .filled(userInputDto.getFilled())
                .addressId(userInputDto.getAddressId())
                .build();
    }
    public static UserOutputDto map(User user, AddressOutputDto addressOutputDto){
        return UserOutputDto.builder()
                .id(user.getId())
                .secretKey(user.getSecretKey())
                .created(user.getCreated())
                .updated(user.getUpdated())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .verifiedAt(user.getVerifiedAt())
                .archivedAt(user.getArchivedAt())
                .status(user.getStatus())
                .filled(user.getFilled())
                .address(addressOutputDto)
                .build();
    }
}
