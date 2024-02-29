package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.UserInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserInputDto userInputDto);
    @Mapping(target = "address", ignore = true)
    UserOutputDto toUserOutputDto(User user);

    default UserOutputDto toUserOutputDtoWithAddress(User user, AddressOutputDto address){
        UserOutputDto userOutputDto = toUserOutputDto(user);
        userOutputDto.setAddress(address);
        return userOutputDto;
    }
}
