package com.milko.data_manager.service;

import com.milko.data_manager.dto.input.VerificationStatusInputDto;
import com.milko.data_manager.dto.output.VerificationStatusOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VerificationStatusService {
    Mono<VerificationStatusOutputDto> create(VerificationStatusInputDto verificationStatusDto);
    Mono<VerificationStatusOutputDto> requestVerification(UUID id);
    Mono<VerificationStatusOutputDto> verify(UUID id);
    Mono<VerificationStatusOutputDto> findById(UUID id);
}
