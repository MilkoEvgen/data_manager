package com.milko.user_provider.dto.input;

import com.milko.user_provider.model.MerchantMember;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpdateMerchantMemberDto {
    private UUID merchantMemberId;
    private MerchantMember merchantMember;
    private String reason;
    private String comment;
}
