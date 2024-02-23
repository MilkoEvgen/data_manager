package com.milko.user_provider.rest;

import com.milko.user_provider.dto.input.UserInputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserRestControllerV1 {
    private final UserService userService;

    @PostMapping
    public Mono<UserOutputDto> createUser(@RequestBody UserInputDto dto){
        return userService.create(dto);
    }

    @PatchMapping("{id}")
    public Mono<UserOutputDto> updateUser(@PathVariable UUID id, @RequestBody UserInputDto dto,
                                          @RequestParam(name = "reason") String reason,
                                          @RequestParam(name = "comment") String comment){
        return userService.update(id, dto, reason, comment);
    }

    @GetMapping("{id}")
    public Mono<UserOutputDto> findUserById(@PathVariable UUID id){
        return userService.findById(id);
    }

    @DeleteMapping("{id}")
    public Mono<Integer> deleteUser(@PathVariable UUID id){
        return userService.deleteById(id);
    }
}
