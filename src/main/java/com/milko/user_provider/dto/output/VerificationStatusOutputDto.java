package com.milko.user_provider.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.milko.user_provider.model.StatusOfVerification;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationStatusOutputDto {
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime updated;
    private UserOutputDto profile;
    private String profileType;
    private String details;
    private StatusOfVerification verificationStatus;
}
