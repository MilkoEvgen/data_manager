package com.milko.user_provider.dto.input;

import com.milko.user_provider.model.ProfileType;
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
public class ProfileHistoryInputDto {
    private UUID id;
    private LocalDateTime created;
    private UUID profileId;
    private ProfileType profileType;
    private String reason;
    private String comment;
    private String changedValues;
}
