package com.milko.user_provider.dto.input;

import com.milko.user_provider.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CountryInputDto {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime created;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updated;
    private String name;
    private String alpha2;
    private String alpha3;
    private Status status;
}
