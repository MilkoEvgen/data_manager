package com.milko.user_provider.dto.input;

import com.milko.user_provider.model.StatusOfVerification;
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
public class VerificationStatusInputDto {
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime updated;
    private UUID profileId;
    private String profileType;
    private String details;
    private StatusOfVerification verificationStatus;
}
