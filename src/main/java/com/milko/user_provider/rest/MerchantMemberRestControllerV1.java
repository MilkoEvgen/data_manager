package com.milko.user_provider.rest;

import com.milko.user_provider.dto.input.MerchantMemberInputDto;
import com.milko.user_provider.dto.input.RegisterMerchantMemberInputDto;
import com.milko.user_provider.dto.input.UpdateMerchantMemberDto;
import com.milko.user_provider.dto.output.MerchantMemberOutputDto;
import com.milko.user_provider.service.MerchantMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name="MerchantMember controller", description="Allows to create, update, get and delete merchant member")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/merchant_members")
public class MerchantMemberRestControllerV1 {
    private final MerchantMemberService memberService;

    @Operation(summary = "Create merchant member",
            description = "Allows to create new merchant member and user")
    @PostMapping
    public Mono<MerchantMemberOutputDto> create(@RequestBody RegisterMerchantMemberInputDto registerDto){
        return memberService.create(registerDto);
    }

    @Operation(summary = "Update merchant member",
            description = "Allows to update merchant member")
    @PatchMapping
    public Mono<MerchantMemberOutputDto> update(@RequestBody UpdateMerchantMemberDto updateMerchantMemberDto){
        return memberService.update(updateMerchantMemberDto);
    }

    @Operation(summary = "Get merchant member",
            description = "Allows to find merchant member by id")
    @GetMapping("{id}")
    public Mono<MerchantMemberOutputDto> getById(@PathVariable UUID id){
        return memberService.findById(id);
    }

    @Operation(summary = "Delete merchant member",
            description = "Allows to delete merchant member by id")
    @DeleteMapping("{id}")
    public Mono<UUID> delete(@PathVariable UUID id){
        return memberService.deleteById(id);
    }
}
