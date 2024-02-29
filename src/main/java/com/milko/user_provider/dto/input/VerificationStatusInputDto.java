package com.milko.user_provider.dto.input;

import com.milko.user_provider.model.StatusOfVerification;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime created;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updated;
    private UUID profileId;
    private String profileType;
    private String details;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private StatusOfVerification verificationStatus;
}
