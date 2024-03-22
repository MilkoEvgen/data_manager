package com.milko.data_manager.dto.input;

import com.milko.data_manager.model.Status;
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
public class MerchantInputDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    private UUID creatorId;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime created;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updated;
    private String companyName;
    private String companyId;
    private String email;
    private String phoneNumber;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime verifiedAt;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime archivedAt;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Status status;
    private Boolean filled;
}
