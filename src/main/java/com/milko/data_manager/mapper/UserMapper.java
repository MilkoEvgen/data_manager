package com.milko.data_manager.mapper;

import com.milko.data_manager.dto.input.UserInputDto;
import com.milko.data_manager.dto.output.AddressOutputDto;
import com.milko.data_manager.dto.output.UserOutputDto;
import com.milko.data_manager.model.User;
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
