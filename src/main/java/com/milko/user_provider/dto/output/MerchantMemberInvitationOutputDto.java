package com.milko.user_provider.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.milko.user_provider.model.Merchant;
import com.milko.user_provider.model.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantMemberInvitationOutputDto {
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime expires;
    private MerchantOutputDto merchant;
    private String firstName;
    private String lastName;
    private String email;
    private Status status;
}
