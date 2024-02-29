package com.milko.user_provider.rest;

import com.milko.user_provider.dto.input.VerificationStatusInputDto;
import com.milko.user_provider.dto.output.VerificationStatusOutputDto;
import com.milko.user_provider.service.VerificationStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name="Verification controller", description="Allows to create, update and get verification status")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/verification_statuses")
public class VerificationRestControllerV1 {
    private final VerificationStatusService verificationStatusService;

    @Operation(summary = "Create verification status",
            description = "Allows to create verification status")
    @PostMapping
    public Mono<VerificationStatusOutputDto> createVerificationStatus(@RequestBody VerificationStatusInputDto dto){
        return verificationStatusService.create(dto);
    }

    @Operation(summary = "Request verification",
            description = "Allows to request verification by id")
    @PostMapping("{id}/request")
    public Mono<VerificationStatusOutputDto> requestVerification(@PathVariable UUID id){
        return verificationStatusService.requestVerification(id);
    }

    @Operation(summary = "Confirm verification",
            description = "Allows to confirm that the user is verified")
    @PostMapping("{id}/verify")
    public Mono<VerificationStatusOutputDto> verify(@PathVariable UUID id){
        return verificationStatusService.verify(id);
    }

    @Operation(summary = "Get verification",
            description = "Allows to find verification status by id")
    @GetMapping("{id}")
    public Mono<VerificationStatusOutputDto> findVerificationStatusById(@PathVariable UUID id){
        return verificationStatusService.findById(id);
    }
}
