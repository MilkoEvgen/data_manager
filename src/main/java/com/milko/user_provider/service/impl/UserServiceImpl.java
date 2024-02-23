package com.milko.user_provider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.milko.user_provider.dto.input.ProfileHistoryInputDto;
import com.milko.user_provider.dto.input.UserInputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.mapper.UserMapper;
import com.milko.user_provider.model.ProfileType;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.model.User;
import com.milko.user_provider.repository.UserRepository;
import com.milko.user_provider.service.AddressService;
import com.milko.user_provider.service.ProfileHistoryService;
import com.milko.user_provider.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ProfileHistoryService profileHistoryService;
    private final AddressService addressService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<UserOutputDto> create(UserInputDto userInputDto) {
        log.info("IN UserService.create(), InputDto = {}", userInputDto);
        User user = UserMapper.map(userInputDto);
        user.setCreated(LocalDateTime.now());
        user.setUpdated(LocalDateTime.now());
        user.setVerifiedAt(LocalDateTime.now());
        user.setArchivedAt(LocalDateTime.now());
        user.setStatus(Status.ACTIVE);
        return addressService.findById(userInputDto.getAddressId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not exists")))
                .flatMap(addressOutputDto -> userRepository.save(user)
                        .map(savedUser -> UserMapper.map(savedUser, addressOutputDto)));
    }

    @Override
    public Mono<UserOutputDto> update(UUID userId, UserInputDto userInputDto, String reason, String comment) {
        log.info("IN UserService.update(), id = {}, InputDto = {}, reason = {}, comment = {}", userId, userInputDto, reason, comment);
        User newUser = UserMapper.map(userInputDto);

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not exists")))
                .flatMap(oldUser -> {
                    ProfileHistoryInputDto historyInputDto = ProfileHistoryInputDto.builder()
                            .created(LocalDateTime.now())
                            .profileId(userId)
                            .profileType(ProfileType.USER)
                            .reason(reason)
                            .comment(comment)
                            .changedValues(getValuesToChange(newUser))
                            .build();
                    return profileHistoryService.create(historyInputDto)
                            .thenReturn(oldUser);
                })
                .flatMap(oldUser -> userRepository.save(setNewValuesToOldUser(newUser, oldUser)))
                .flatMap(updatedUser -> addressService.findById(updatedUser.getAddressId())
                                .flatMap(addressOutputDto -> Mono.just(UserMapper.map(updatedUser, addressOutputDto))));
    }

    @Override
    public Mono<UserOutputDto> findById(UUID id) {
        log.info("IN UserService.findById(), id = {}", id);
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not exists")))
                .flatMap(user -> addressService.findById(user.getAddressId())
                        .flatMap(addressOutputDto -> Mono.just(UserMapper.map(user, addressOutputDto))));
    }

    @Override
    public Mono<Integer> deleteById(UUID id) {
        log.info("IN UserService.deleteById(), id = {}", id);
        return userRepository.updateStatusToDeletedById(id)
                .flatMap(integer -> {
                    if (integer == 0){
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not exists"));
                    }
                    return Mono.just(integer);
                });
    }

    private String getValuesToChange(User user){
        try {
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private User setNewValuesToOldUser(User newUser, User oldUser){
        oldUser.setUpdated(LocalDateTime.now());

        if (!Objects.equals(newUser.getSecretKey(), null)){
            oldUser.setSecretKey(newUser.getSecretKey());
        }
        if (!Objects.equals(newUser.getFirstName(), null)){
            oldUser.setFirstName(newUser.getFirstName());
        }
        if (!Objects.equals(newUser.getLastName(), null)){
            oldUser.setLastName(newUser.getLastName());
        }
        if (!Objects.equals(newUser.getAddressId(), null)){
            oldUser.setAddressId(newUser.getAddressId());
        }
        return oldUser;
    }
}
