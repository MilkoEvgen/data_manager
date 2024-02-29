package com.milko.user_provider.rest;

import com.milko.user_provider.dto.input.UpdateUserInputDto;
import com.milko.user_provider.dto.input.UserInputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name="User controller", description="Allows to update, get and delete user")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserRestControllerV1 {
    private final UserService userService;

    @Operation(summary = "Update user",
            description = "Allows to update user")
    @PatchMapping
    public Mono<UserOutputDto> updateUser(@RequestBody UpdateUserInputDto updateUserInputDto){
        return userService.update(updateUserInputDto);
    }

    @Operation(summary = "Get user",
            description = "Allows to find user by id")
    @GetMapping("{id}")
    public Mono<UserOutputDto> findUserById(@PathVariable UUID id){
        return userService.findById(id);
    }

    @Operation(summary = "Delete user",
            description = "Allows to delete user by id")
    @DeleteMapping("{id}")
    public Mono<UUID> deleteUser(@PathVariable UUID id){
        return userService.deleteById(id);
    }
}
