package com.milko.user_provider.service.impl;

import com.milko.user_provider.dto.input.VerificationStatusInputDto;
import com.milko.user_provider.dto.output.VerificationStatusOutputDto;
import com.milko.user_provider.mapper.VerificationStatusMapper;
import com.milko.user_provider.model.StatusOfVerification;
import com.milko.user_provider.model.User;
import com.milko.user_provider.model.VerificationStatus;
import com.milko.user_provider.repository.UserRepository;
import com.milko.user_provider.repository.VerificationStatusRepository;
import com.milko.user_provider.service.UserService;
import com.milko.user_provider.service.VerificationStatusService;
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
public class VerificationStatusServiceImpl implements VerificationStatusService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final VerificationStatusRepository verificationStatusRepository;

    @Override
    public Mono<VerificationStatusOutputDto> create(VerificationStatusInputDto verificationStatusInputDto) {
        log.info("IN VerificationStatusService.create(), InputDto = {}", verificationStatusInputDto);
        VerificationStatus verificationStatus = VerificationStatusMapper.map(verificationStatusInputDto);
        verificationStatus.setCreated(LocalDateTime.now());
        verificationStatus.setUpdated(LocalDateTime.now());
        verificationStatusInputDto.setVerificationStatus(StatusOfVerification.NOT_VERIFIED);
        return userService.findById(verificationStatusInputDto.getProfileId())
                .flatMap(userOutputDto -> verificationStatusRepository.save(VerificationStatusMapper.map(verificationStatusInputDto))
                        .flatMap(savedStatus -> Mono.just(VerificationStatusMapper.map(savedStatus, userOutputDto))));
    }

    @Override
    public Mono<VerificationStatusOutputDto> requestVerification(UUID id) {
        log.info("IN VerificationStatusService.requestVerification(), id = {}", id);
        return verificationStatusRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Verification status not exists")))
                .flatMap(verificationStatus -> {
                    verificationStatus.setVerificationStatus(StatusOfVerification.VERIFICATION_REQUESTED);
                    return verificationStatusRepository.save(verificationStatus);
                })
                .flatMap(savedStatus -> userService.findById(savedStatus.getProfileId())
                        .flatMap(userOutputDto -> Mono.just(VerificationStatusMapper.map(savedStatus, userOutputDto))));
    }

    //Здесь использую и userService и userRepository, правильно ли?
    @Override
    public Mono<VerificationStatusOutputDto> verify(UUID id) {
        log.info("IN VerificationStatusService.verify(), id = {}", id);
        return verificationStatusRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Verification status not exists")))
                .flatMap(verificationStatus -> {
                    verificationStatus.setVerificationStatus(StatusOfVerification.VERIFIED);
                    return verificationStatusRepository.save(verificationStatus);
                })
                .flatMap(savedStatus ->
                        userRepository.findById(savedStatus.getProfileId())
                                .flatMap(user -> {
                                    user.setVerifiedAt(LocalDateTime.now());
                                    return userRepository.save(user);
                                })
                                .then(Mono.just(savedStatus))
                )
                .flatMap(savedStatus -> userService.findById(savedStatus.getProfileId())
                        .flatMap(userOutputDto -> Mono.just(VerificationStatusMapper.map(savedStatus, userOutputDto))));
    }

    @Override
    public Mono<VerificationStatusOutputDto> findById(UUID id) {
        log.info("IN VerificationStatusService.findById(), id = {}", id);
        return verificationStatusRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Verification status not exists")))
                .flatMap(verificationStatus -> userService.findById(verificationStatus.getProfileId())
                        .flatMap(userOutputDto -> Mono.just(VerificationStatusMapper.map(verificationStatus, userOutputDto))));
    }

}