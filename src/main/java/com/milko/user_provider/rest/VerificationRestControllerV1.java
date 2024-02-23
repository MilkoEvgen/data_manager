package com.milko.user_provider.rest;

import com.milko.user_provider.dto.input.VerificationStatusInputDto;
import com.milko.user_provider.dto.output.VerificationStatusOutputDto;
import com.milko.user_provider.service.VerificationStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/verification_statuses")
public class VerificationRestControllerV1 {
    private final VerificationStatusService verificationStatusService;

    @PostMapping
    public Mono<VerificationStatusOutputDto> createVerificationStatus(@RequestBody VerificationStatusInputDto dto){
        return verificationStatusService.create(dto);
    }

    @PostMapping("{id}/request")
    public Mono<VerificationStatusOutputDto> requestVerification(@PathVariable UUID id){
        return verificationStatusService.requestVerification(id);
    }

    @PostMapping("{id}/verify")
    public Mono<VerificationStatusOutputDto> verify(@PathVariable UUID id){
        return verificationStatusService.verify(id);
    }

    @GetMapping("{id}")
    public Mono<VerificationStatusOutputDto> findVerificationStatusById(@PathVariable UUID id){
        return verificationStatusService.findById(id);
    }
}
