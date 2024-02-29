package com.milko.user_provider.dto.input;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class RegisterMerchantMemberInputDto {
    private String secretKey;
    private String firstName;
    private String lastName;
    private UUID addressId;
    private UUID merchantId;
    private String memberRole;
}
