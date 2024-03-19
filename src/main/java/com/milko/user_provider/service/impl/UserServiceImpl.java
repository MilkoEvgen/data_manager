package com.milko.user_provider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milko.user_provider.dto.input.UpdateUserInputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.exceptions.EntityNotFoundException;
import com.milko.user_provider.mapper.UserMapper;
import com.milko.user_provider.model.ProfileHistory;
import com.milko.user_provider.model.ProfileType;
import com.milko.user_provider.model.User;
import com.milko.user_provider.repository.ProfileHistoryRepository;
import com.milko.user_provider.repository.UserRepository;
import com.milko.user_provider.service.AddressService;
import com.milko.user_provider.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ProfileHistoryRepository profileHistoryRepository;
    private final AddressService addressService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    @Override
    public Mono<UserOutputDto> update(UpdateUserInputDto updateUserInputDto) {
        log.info("IN UserService.update(), updateUserInputDto = {}", updateUserInputDto);
        User newUser = updateUserInputDto.getUser();

        return userRepository.findById(updateUserInputDto.getUserId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not exists")))
                .flatMap(oldUser -> {
                    ProfileHistory history = ProfileHistory.builder()
                            .created(LocalDateTime.now())
                            .userId(updateUserInputDto.getUserId())
                            .profileType(ProfileType.USER)
                            .reason(updateUserInputDto.getReason())
                            .comment(updateUserInputDto.getComment())
                            .changedValues(getValuesToChange(newUser))
                            .build();
                    return profileHistoryRepository.save(history)
                            .thenReturn(oldUser);
                })
                .flatMap(oldUser -> userRepository.save(setNewValuesToOldUser(newUser, oldUser)))
                .flatMap(updatedUser -> addressService.findById(updatedUser.getAddressId())
                                .flatMap(addressOutputDto -> Mono.just(userMapper.toUserOutputDtoWithAddress(updatedUser, addressOutputDto))));
    }

    @Override
    public Mono<UserOutputDto> findById(UUID id) {
        log.info("IN UserService.findById(), id = {}", id);
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not exists")))
                .flatMap(user -> addressService.findById(user.getAddressId())
                        .flatMap(addressOutputDto -> Mono.just(userMapper.toUserOutputDtoWithAddress(user, addressOutputDto))));
    }

    @Override
    public Mono<UUID> deleteById(UUID id) {
        log.info("IN UserService.deleteById(), id = {}", id);
        return userRepository.updateStatusToDeletedById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not exists")));
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

        if (!Objects.equals(newUser.getAuthServiceId(), null)){
            oldUser.setAuthServiceId(newUser.getAuthServiceId());
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
