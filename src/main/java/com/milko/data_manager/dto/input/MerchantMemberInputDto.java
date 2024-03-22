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
public class MerchantMemberInputDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    private UUID userId;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime created;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updated;
    private UUID merchantId;
    private String memberRole;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Status status;
}
