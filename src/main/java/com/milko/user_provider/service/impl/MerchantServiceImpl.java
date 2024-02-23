package com.milko.user_provider.service.impl;

import com.milko.user_provider.dto.input.MerchantInputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.mapper.MerchantMapper;
import com.milko.user_provider.model.Merchant;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.repository.MerchantRepository;
import com.milko.user_provider.service.MerchantService;
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
public class MerchantServiceImpl implements MerchantService {
    private final MerchantRepository merchantRepository;
    private final UserService userService;

    @Override
    public Mono<MerchantOutputDto> create(MerchantInputDto merchantInputDto) {
        log.info("IN MerchantService.create(), InputDto = {}", merchantInputDto);
        Merchant merchant = MerchantMapper.map(merchantInputDto);
        merchant.setCreated(LocalDateTime.now());
        merchant.setUpdated(LocalDateTime.now());
        merchant.setVerifiedAt(LocalDateTime.now());
        merchant.setArchivedAt(LocalDateTime.now());
        merchant.setStatus(Status.ACTIVE);
        return userService.findById(merchantInputDto.getCreatorId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not exists")))
                .flatMap(userOutputDto -> merchantRepository.save(merchant)
                        .flatMap(savedMerchant -> Mono.just(MerchantMapper.map(savedMerchant, userOutputDto))));
    }

    @Override
    public Mono<MerchantOutputDto> update(UUID id, MerchantInputDto merchantInputDto) {
        log.info("IN MerchantService.update(), id = {}, InputDto = {}", id, merchantInputDto);
        Merchant newMerchant = MerchantMapper.map(merchantInputDto);

        return merchantRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not exists")))
                .flatMap(oldMerchant -> merchantRepository.save(setNewValuesToOldMerchant(newMerchant, oldMerchant))
                        .flatMap(savedMerchant -> userService.findById(savedMerchant.getCreatorId())
                                .flatMap(userOutputDto -> Mono.just(MerchantMapper.map(savedMerchant, userOutputDto)))));
    }

    @Override
    public Mono<MerchantOutputDto> findById(UUID id) {
        log.info("IN MerchantService.findById(), id = {}", id);
        return merchantRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not exists")))
                .flatMap(merchant -> userService.findById(merchant.getCreatorId())
                        .flatMap(userOutputDto -> Mono.just(MerchantMapper.map(merchant, userOutputDto))));
    }

    @Override
    public Mono<Integer> deleteById(UUID id) {
        log.info("IN MerchantService.deleteById(), id = {}", id);
        return merchantRepository.updateStatusToDeletedById(id)
                .flatMap(integer -> {
                    if (integer == 0){
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not exists"));
                    }
                    return Mono.just(integer);
                });
    }

    private Merchant setNewValuesToOldMerchant(Merchant newMerchant, Merchant oldMerchant){
        oldMerchant.setUpdated(LocalDateTime.now());

        if (!Objects.equals(newMerchant.getCompanyName(), null)){
            oldMerchant.setCompanyName(newMerchant.getCompanyName());
        }
        if (!Objects.equals(newMerchant.getCompanyId(), null)){
            oldMerchant.setCompanyId(newMerchant.getCompanyId());
        }
        if (!Objects.equals(newMerchant.getEmail(), null)){
            oldMerchant.setEmail(newMerchant.getEmail());
        }
        if (!Objects.equals(newMerchant.getPhoneNumber(), null)){
            oldMerchant.setPhoneNumber(newMerchant.getPhoneNumber());
        }
        return oldMerchant;
    }
}
