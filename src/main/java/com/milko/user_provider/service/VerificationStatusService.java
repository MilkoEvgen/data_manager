package com.milko.user_provider.service;

import com.milko.user_provider.dto.input.VerificationStatusInputDto;
import com.milko.user_provider.dto.output.VerificationStatusOutputDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VerificationStatusService {
    Mono<VerificationStatusOutputDto> create(VerificationStatusInputDto verificationStatusDto);
    Mono<VerificationStatusOutputDto> requestVerification(UUID id);
    Mono<VerificationStatusOutputDto> verify(UUID id);
    Mono<VerificationStatusOutputDto> findById(UUID id);
}
