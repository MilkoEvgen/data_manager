package com.milko.data_manager.dto.input;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class RegisterMerchantMemberInputDto {
    private UUID authServiceId;
    private String firstName;
    private String lastName;
    private UUID addressId;
    private UUID merchantId;
    private String memberRole;
}
