package com.milko.user_provider.dto.input;

import com.milko.user_provider.model.User;
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
