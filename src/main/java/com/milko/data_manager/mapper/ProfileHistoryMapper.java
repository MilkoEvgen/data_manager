package com.milko.data_manager.mapper;

import com.milko.data_manager.dto.input.ProfileHistoryInputDto;
import com.milko.data_manager.dto.output.ProfileHistoryOutputDto;
import com.milko.data_manager.dto.output.UserOutputDto;
import com.milko.data_manager.model.ProfileHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileHistoryMapper {

    ProfileHistory toProfileHistory(ProfileHistoryInputDto profileHistory);

    @Mapping(target = "user", ignore = true)
    ProfileHistoryOutputDto toProfileHistory(ProfileHistory profileHistory);

    default ProfileHistoryOutputDto toProfileHistoryWithUser(ProfileHistory profileHistory, UserOutputDto userOutputDto){
        ProfileHistoryOutputDto historyOutputDto = toProfileHistory(profileHistory);
        historyOutputDto.setUser(userOutputDto);
        return historyOutputDto;
    }
}
