package com.milko.user_provider.dto.output;

import com.milko.user_provider.model.ProfileType;
import com.milko.user_provider.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProfileHistoryOutputDto {
    private UUID id;
    private LocalDateTime created;
    private UserOutputDto profile;
    private ProfileType profileType;
    private String reason;
    private String comment;
    private String changedValues;
}
