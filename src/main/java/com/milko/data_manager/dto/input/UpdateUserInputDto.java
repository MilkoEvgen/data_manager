package com.milko.data_manager.dto.input;

import com.milko.data_manager.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateUserInputDto {
    private UUID userId;
    private User user;
    private String reason;
    private String comment;
}
