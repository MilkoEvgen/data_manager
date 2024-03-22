package com.milko.data_manager.dto.input;

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
public class AddressInputDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime created;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updated;
    private String address;
    private String zipCode;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime archived;
    private String city;
    private String state;
    private Integer countryId;
}
