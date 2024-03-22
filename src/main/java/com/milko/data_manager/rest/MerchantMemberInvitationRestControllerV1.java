package com.milko.data_manager.rest;

import com.milko.data_manager.dto.input.MerchantMemberInvitationInputDto;
import com.milko.data_manager.dto.output.MerchantMemberInvitationOutputDto;
import com.milko.data_manager.service.MerchantMemberInvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name="MerchantMemberInvitation controller", description="Allows to create, get and delete invitation for merchant member")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invite_member")
public class MerchantMemberInvitationRestControllerV1 {
    private final MerchantMemberInvitationService invitationService;

    @Operation(summary = "Create invitation",
            description = "Allows to create new invitation for merchant member")
    @PostMapping
    public Mono<MerchantMemberInvitationOutputDto> create(@RequestBody MerchantMemberInvitationInputDto invitationInputDto){
        return invitationService.create(invitationInputDto);
    }

    @Operation(summary = "Find all invitations",
            description = "Allows to find all invitations for merchant by merchant id")
    @GetMapping("{merchantId}/find_all_by_merchant")
    public Flux<MerchantMemberInvitationOutputDto> findAllByMerchantId(@PathVariable UUID merchantId){
        return invitationService.findAllByMerchantId(merchantId);
    }

    @Operation(summary = "Delete invitation",
            description = "Allows delete invitation by id")
    @DeleteMapping("{id}")
    public Mono<UUID> deleteById(@PathVariable UUID id){
        return invitationService.deleteById(id);
    }

}
