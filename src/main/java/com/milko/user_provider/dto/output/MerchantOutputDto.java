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
public class MerchantOutputDto {
    private UUID id;
    private UserOutputDto creator;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String companyName;
    private String companyId;
    private String email;
    private String phoneNumber;
    private LocalDateTime verifiedAt;
    private LocalDateTime archivedAt;
    private Status status;
    private Boolean filled;
}
