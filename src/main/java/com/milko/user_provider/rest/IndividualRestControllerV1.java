package com.milko.user_provider.rest;

import com.milko.user_provider.dto.input.RegisterIndividualInputDto;
import com.milko.user_provider.dto.input.UpdateIndividualDto;
import com.milko.user_provider.dto.output.IndividualOutputDto;
import com.milko.user_provider.service.IndividualService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name="Individual controller", description="Allows to create, update, get and delete individual user")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/individuals")
public class IndividualRestControllerV1 {
    private final IndividualService individualService;

    @Operation(summary = "Create individual",
            description = "Allows to create new individual and new user")
    @PostMapping
    public Mono<IndividualOutputDto> createIndividuals(@RequestBody RegisterIndividualInputDto dto){
        return individualService.create(dto);
    }

    @Operation(summary = "Update individual",
            description = "Allows to update individual")
    @PatchMapping
    public Mono<IndividualOutputDto> updateIndividuals(@RequestBody UpdateIndividualDto updateIndividualDto){
        return individualService.update(updateIndividualDto);
    }

    @Operation(summary = "Get individual",
            description = "Allows to find individual by id")
    @GetMapping("{id}")
    public Mono<IndividualOutputDto> findIndividualsById(@PathVariable UUID id){
        return individualService.findById(id);
    }

    @Operation(summary = "Delete individual",
            description = "Allows to delete individual by id")
    @DeleteMapping("{id}")
    public Mono<UUID> deleteIndividuals(@PathVariable UUID id){
        return individualService.deleteById(id);
    }
}
