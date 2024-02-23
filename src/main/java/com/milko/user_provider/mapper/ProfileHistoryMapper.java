package com.milko.user_provider.mapper;

import com.milko.user_provider.dto.input.ProfileHistoryInputDto;
import com.milko.user_provider.dto.output.ProfileHistoryOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.ProfileHistory;

public class ProfileHistoryMapper {

    public static ProfileHistory map(ProfileHistoryInputDto profileHistory){
        return ProfileHistory.builder()
                .id(profileHistory.getId())
                .created(profileHistory.getCreated())
                .profileId(profileHistory.getProfileId())
                .profileType(profileHistory.getProfileType())
                .reason(profileHistory.getReason())
                .comment(profileHistory.getComment())
                .changedValues(profileHistory.getChangedValues())
                .build();
    }


    public static ProfileHistoryOutputDto map(ProfileHistory profileHistory, UserOutputDto userOutputDto){
        return ProfileHistoryOutputDto.builder()
                .id(profileHistory.getId())
                .created(profileHistory.getCreated())
                .profile(userOutputDto)
                .profileType(profileHistory.getProfileType())
                .reason(profileHistory.getReason())
                .comment(profileHistory.getComment())
                .changedValues(profileHistory.getChangedValues())
                .build();
    }
}
