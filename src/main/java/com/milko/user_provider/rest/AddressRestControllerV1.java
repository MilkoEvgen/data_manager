package com.milko.user_provider.rest;

import com.milko.user_provider.dto.input.AddressInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/addresses")
public class AddressRestControllerV1 {
    private final AddressService addressService;

    @PostMapping
    public Mono<AddressOutputDto> createAddress(@RequestBody AddressInputDto dto){
        return addressService.create(dto);
    }

    @PatchMapping("{id}")
    public Mono<AddressOutputDto> updateAddress(@PathVariable UUID id, @RequestBody AddressInputDto dto){
        return addressService.update(id, dto);
    }

    @GetMapping("{id}")
    public Mono<AddressOutputDto> findAddressById(@PathVariable UUID id){
        return addressService.findById(id);
    }

}
