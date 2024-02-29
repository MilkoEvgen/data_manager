package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.ProfileHistoryInputDto;
import com.milko.user_provider.dto.output.ProfileHistoryOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.ProfileHistory;
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
