package com.milko.data_manager.rest;

import com.milko.data_manager.dto.input.AddressInputDto;
import com.milko.data_manager.dto.output.AddressOutputDto;
import com.milko.data_manager.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name="Address controller", description="Allows to create, update and get address")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/addresses")
public class AddressRestControllerV1 {
    private final AddressService addressService;

    @Operation(summary = "Create address",
            description = "Allows to create new address")
    @PostMapping
    public Mono<AddressOutputDto> createAddress(@RequestBody AddressInputDto dto){
        return addressService.create(dto);
    }

    @Operation(summary = "Update address",
            description = "Allows to update address")
    @PatchMapping("{id}")
    public Mono<AddressOutputDto> updateAddress(@PathVariable UUID id, @RequestBody AddressInputDto dto){
        return addressService.update(id, dto);
    }

    @Operation(summary = "Get address",
            description = "Allows to find address by id")
    @GetMapping("{id}")
    public Mono<AddressOutputDto> findAddressById(@PathVariable UUID id){
        return addressService.findById(id);
    }

}
