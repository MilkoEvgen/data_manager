package com.milko.data_manager.dto.input;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MerchantMemberInvitationInputDto {
    private Long validForDays;
    private UUID merchantId;
    private String firstName;
    private String lastName;
    private String email;
}
