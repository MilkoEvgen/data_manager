package com.milko.user_provider.rest;

import com.milko.user_provider.dto.input.IndividualsInputDto;
import com.milko.user_provider.dto.output.IndividualsOutputDto;
import com.milko.user_provider.service.IndividualsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/individuals")
public class IndividualsRestControllerV1 {
    private final IndividualsService individualsService;

    @PostMapping
    public Mono<IndividualsOutputDto> createIndividuals(@RequestBody IndividualsInputDto dto){
        return individualsService.create(dto);
    }

    @PatchMapping("{id}")
    public Mono<IndividualsOutputDto> updateIndividuals(@PathVariable UUID id, @RequestBody IndividualsInputDto dto,
                                                        @RequestParam(name = "reason") String reason,
                                                        @RequestParam(name = "comment") String comment){
        return individualsService.update(id, dto, reason, comment);
    }

    @GetMapping("{id}")
    public Mono<IndividualsOutputDto> findIndividualsById(@PathVariable UUID id){
        return individualsService.findById(id);
    }

    @DeleteMapping("{id}")
    public Mono<Boolean> deleteIndividuals(@PathVariable UUID id){
        return individualsService.deleteById(id);
    }
}
