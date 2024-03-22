package com.milko.data_manager.dto.input;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateIndividualDto {
    private UUID individualId;
    private IndividualInputDto individualInputDto;
    private String reason;
    private String comment;
}
