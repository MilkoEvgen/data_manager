package com.milko.data_manager.service.impl;

import com.milko.data_manager.dto.input.VerificationStatusInputDto;
import com.milko.data_manager.dto.output.VerificationStatusOutputDto;
import com.milko.data_manager.exceptions.EntityNotFoundException;
import com.milko.data_manager.mapper.VerificationStatusMapper;
import com.milko.data_manager.model.StatusOfVerification;
import com.milko.data_manager.model.VerificationStatus;
import com.milko.data_manager.repository.UserRepository;
import com.milko.data_manager.repository.VerificationStatusRepository;
import com.milko.data_manager.service.UserService;
import com.milko.data_manager.service.VerificationStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationStatusServiceImpl implements VerificationStatusService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final VerificationStatusRepository verificationStatusRepository;
    private final VerificationStatusMapper verificationMapper;

    @Override
    public Mono<VerificationStatusOutputDto> create(VerificationStatusInputDto verificationStatusInputDto) {
        log.info("IN VerificationStatusService.create(), InputDto = {}", verificationStatusInputDto);
        VerificationStatus verificationStatus = verificationMapper.toVerification(verificationStatusInputDto);
        verificationStatus.setCreated(LocalDateTime.now());
        verificationStatus.setUpdated(LocalDateTime.now());
        verificationStatus.setVerificationStatus(StatusOfVerification.NOT_VERIFIED);
        return userService.findById(verificationStatusInputDto.getProfileId())
                .flatMap(userOutputDto -> verificationStatusRepository.save(verificationStatus)
                        .flatMap(savedStatus -> Mono.just(verificationMapper.toVerificationOutputDtoWithUser(savedStatus, userOutputDto))));
    }

    @Override
    public Mono<VerificationStatusOutputDto> requestVerification(UUID id) {
        log.info("IN VerificationStatusService.requestVerification(), id = {}", id);
        return verificationStatusRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Verification status not exists")))
                .flatMap(verificationStatus -> {
                    verificationStatus.setVerificationStatus(StatusOfVerification.VERIFICATION_REQUESTED);
                    verificationStatus.setUpdated(LocalDateTime.now());
                    return verificationStatusRepository.save(verificationStatus);
                })
                .flatMap(savedStatus -> userService.findById(savedStatus.getProfileId())
                        .flatMap(userOutputDto -> Mono.just(verificationMapper.toVerificationOutputDtoWithUser(savedStatus, userOutputDto))));
    }

    @Override
    public Mono<VerificationStatusOutputDto> verify(UUID id) {
        log.info("IN VerificationStatusService.verify(), id = {}", id);
        return verificationStatusRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Verification status not exists")))
                .flatMap(verificationStatus -> {
                    verificationStatus.setVerificationStatus(StatusOfVerification.VERIFIED);
                    verificationStatus.setUpdated(LocalDateTime.now());
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
                        .flatMap(userOutputDto -> Mono.just(verificationMapper.toVerificationOutputDtoWithUser(savedStatus, userOutputDto))));
    }

    @Override
    public Mono<VerificationStatusOutputDto> findById(UUID id) {
        log.info("IN VerificationStatusService.findById(), id = {}", id);
        return verificationStatusRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Verification status not exists")))
                .flatMap(verificationStatus -> userService.findById(verificationStatus.getProfileId())
                        .flatMap(userOutputDto -> Mono.just(verificationMapper.toVerificationOutputDtoWithUser(verificationStatus, userOutputDto))));
    }

}
