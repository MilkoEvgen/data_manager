package com.milko.user_provider.rest;

import com.milko.user_provider.dto.input.MerchantInputDto;
import com.milko.user_provider.dto.input.MerchantMemberInputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/merchants")
public class MerchantRestControllerV1 {
    private final MerchantService merchantService;

    @PostMapping
    public Mono<MerchantOutputDto> createMerchant(@RequestBody MerchantInputDto dto){
        return merchantService.create(dto);
    }

    @PatchMapping("{id}")
    public Mono<MerchantOutputDto> updateMerchant(@PathVariable UUID id, @RequestBody MerchantInputDto dto){
        return merchantService.update(id, dto);
    }

    @GetMapping("{id}")
    public Mono<MerchantOutputDto> findMerchantById(@PathVariable UUID id){
        return merchantService.findById(id);
    }

    @DeleteMapping("{id}")
    public Mono<Integer> deleteMerchant(@PathVariable UUID id){
        return merchantService.deleteById(id);
    }
}
