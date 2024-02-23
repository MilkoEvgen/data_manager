package com.milko.user_provider.dto.output;

import com.milko.user_provider.model.Status;
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
public class MerchantMemberOutputDto {
    private UUID id;
    private UserOutputDto user;
    private LocalDateTime created;
    private LocalDateTime updated;
    private MerchantOutputDto merchant;
    private String memberRole;
    private Status status;
}
