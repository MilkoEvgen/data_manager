package com.milko.user_provider.rest;

import com.milko.user_provider.dto.output.ProfileHistoryOutputDto;
import com.milko.user_provider.service.ProfileHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/history")
public class ProfileHistoryRestControllerV1 {
    private final ProfileHistoryService profileHistoryService;

    @GetMapping("{id}/profile")
    public Flux<ProfileHistoryOutputDto> getAllHistoryByProfileId(@PathVariable UUID id){
        return profileHistoryService.getAllHistoryByProfileId(id);
    }

    @GetMapping("{id}")
    public Mono<ProfileHistoryOutputDto> findById(@PathVariable UUID id){
        return profileHistoryService.findById(id);
    }

}
