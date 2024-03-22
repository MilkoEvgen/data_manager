package com.milko.data_manager.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.milko.data_manager.model.ProfileType;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileHistoryOutputDto {
    private UUID id;
    private LocalDateTime created;
    private UserOutputDto user;
    private ProfileType profileType;
    private String reason;
    private String comment;
    private String changedValues;
}
