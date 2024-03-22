package com.milko.data_manager.dto.input;

import com.milko.data_manager.model.MerchantMember;
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
