package com.milko.user_provider.rest;

import com.milko.user_provider.dto.input.MerchantMemberInputDto;
import com.milko.user_provider.dto.output.MerchantMemberOutputDto;
import com.milko.user_provider.service.MerchantMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/merchant_members")
public class MerchantMemberRestControllerV1 {
    private final MerchantMemberService memberService;

    @PostMapping
    public Mono<MerchantMemberOutputDto> create(@RequestBody MerchantMemberInputDto memberInputDto){
        return memberService.create(memberInputDto);
    }

    @PatchMapping("{id}")
    public Mono<MerchantMemberOutputDto> update(@PathVariable UUID id, @RequestBody MerchantMemberInputDto memberInputDto,
                                                @RequestParam(name = "reason") String reason,
                                                @RequestParam(name = "comment") String comment){
        return memberService.update(id, memberInputDto, reason, comment);
    }

    @GetMapping("{id}")
    public Mono<MerchantMemberOutputDto> getById(@PathVariable UUID id){
        return memberService.findById(id);
    }

    @DeleteMapping("{id}")
    public Mono<Integer> delete(@PathVariable UUID id){
        return memberService.deleteById(id);
    }
}
