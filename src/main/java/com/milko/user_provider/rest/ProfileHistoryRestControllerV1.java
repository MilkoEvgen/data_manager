package com.milko.user_provider.rest;

import com.milko.user_provider.dto.output.ProfileHistoryOutputDto;
import com.milko.user_provider.service.ProfileHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name="ProfileHistory controller", description="Allows to get profile history")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/history")
public class ProfileHistoryRestControllerV1 {
    private final ProfileHistoryService profileHistoryService;

    @Operation(summary = "Get list of history",
            description = "Allows to get all history by user id")
    @GetMapping("{id}/profile")
    public Flux<ProfileHistoryOutputDto> getAllHistoryByUserId(@PathVariable UUID id){
        return profileHistoryService.getAllHistoryByUserId(id);
    }

    @Operation(summary = "Get history",
            description = "Allows to get history by id")
    @GetMapping("{id}")
    public Mono<ProfileHistoryOutputDto> findById(@PathVariable UUID id){
        return profileHistoryService.findById(id);
    }

}
