package com.milko.data_manager.dto.input;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class RegisterIndividualInputDto {
    private UUID authServiceId;
    private String firstName;
    private String lastName;
    private UUID addressId;
    private String passportNumber;
    private String phoneNumber;
    private String email;
}
