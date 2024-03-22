package com.milko.data_manager.rest;

import com.milko.data_manager.dto.input.MerchantInputDto;
import com.milko.data_manager.dto.output.MerchantOutputDto;
import com.milko.data_manager.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name="Merchant controller", description="Allows to create, update, get and delete merchant")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/merchants")
public class MerchantRestControllerV1 {
    private final MerchantService merchantService;

    @Operation(summary = "Create merchant",
            description = "Allows to create new merchant")
    @PostMapping
    public Mono<MerchantOutputDto> createMerchant(@RequestBody MerchantInputDto dto){
        log.info("IN MerchantService.create(), InputDto = {}", dto);
        return merchantService.create(dto);
    }

    @Operation(summary = "Update merchant",
            description = "Allows to update merchant")
    @PatchMapping("{id}")
    public Mono<MerchantOutputDto> updateMerchant(@PathVariable UUID id, @RequestBody MerchantInputDto dto){
        return merchantService.update(id, dto);
    }

    @Operation(summary = "Get merchant",
            description = "Allows to find merchant by id")
    @GetMapping("{id}")
    public Mono<MerchantOutputDto> findMerchantById(@PathVariable UUID id){
        return merchantService.findById(id);
    }

    @Operation(summary = "Delete merchant",
            description = "Allows to delete merchant by id")
    @DeleteMapping("{id}")
    public Mono<UUID> deleteMerchant(@PathVariable UUID id){
        return merchantService.deleteById(id);
    }
}
