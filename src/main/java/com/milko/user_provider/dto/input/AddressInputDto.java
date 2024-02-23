package com.milko.user_provider.dto.input;

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
public class AddressInputDto {
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String address;
    private String zipCode;
    private LocalDateTime archived;
    private String city;
    private String state;
    private Integer countryId;
}
