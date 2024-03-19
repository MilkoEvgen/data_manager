package com.milko.user_provider;

import com.milko.user_provider.dto.input.MerchantInputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.exceptions.EntityNotFoundException;
import com.milko.user_provider.mapper.MerchantMapper;
import com.milko.user_provider.model.Merchant;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.repository.MerchantRepository;
import com.milko.user_provider.service.UserService;
import com.milko.user_provider.service.impl.MerchantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class MerchantServiceImplTest {
    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private UserService userService;
    @Mock
    private MerchantMapper merchantMapper;
    @InjectMocks
    private MerchantServiceImpl merchantService;

    private MerchantInputDto merchantInputDto;
    private UserOutputDto userOutputDto;
    private Merchant merchant;
    private MerchantOutputDto merchantOutputDto;

    @BeforeEach
    public void init() {
        merchantInputDto = MerchantInputDto.builder()
                .creatorId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .build();
        userOutputDto = UserOutputDto.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .firstName("firstName")
                .lastName("lastName")
                .status(Status.ACTIVE)
                .build();
        merchant = Merchant.builder()
                .id(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .creatorId(UUID.randomUUID())
                .companyId("company id")
                .companyName("company name")
                .phoneNumber("phone number")
                .email("email")
                .filled(true)
                .status(Status.ACTIVE)
                .build();
        merchantOutputDto = MerchantOutputDto.builder()
                .id(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .creator(userOutputDto)
                .companyId("company id")
                .companyName("company name")
                .phoneNumber("phone number")
                .email("email")
                .filled(true)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    public void createShouldReturnMerchantOutputDto() {
        Mockito.when(merchantMapper.toMerchant(any(MerchantInputDto.class))).thenReturn(merchant);
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(merchantRepository.save(any(Merchant.class))).thenReturn(Mono.just(merchant));
        Mockito.when(merchantMapper.toMerchantOutputDtoWithCreator(any(Merchant.class), any(UserOutputDto.class))).thenReturn(merchantOutputDto);
        Mono<MerchantOutputDto> result = merchantService.create(merchantInputDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getCreator().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getCreator().getFirstName().equals("firstName") &&
                            resultDto.getCreator().getLastName().equals("lastName") &&
                            resultDto.getCreator().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getCompanyId().equals("company id") &&
                            resultDto.getCompanyName().equals("company name") &&
                            resultDto.getPhoneNumber().equals("phone number") &&
                            resultDto.getEmail().equals("email") &&
                            resultDto.getFilled().equals(true) &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(merchantMapper).toMerchant(any(MerchantInputDto.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(merchantRepository).save(any(Merchant.class));
        Mockito.verify(merchantMapper).toMerchantOutputDtoWithCreator(any(Merchant.class), any(UserOutputDto.class));
    }

    @Test
    public void createShouldThrowEntityNotFoundExceptionIfUserNotExists() {
        Mockito.when(merchantMapper.toMerchant(any(MerchantInputDto.class))).thenReturn(merchant);
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<MerchantOutputDto> result = merchantService.create(merchantInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("User not exists"))
                .verify();
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(merchantMapper).toMerchant(any(MerchantInputDto.class));
    }

    @Test
    public void updateShouldReturnMerchantOutputDto() {
        Mockito.when(merchantMapper.toMerchant(any(MerchantInputDto.class))).thenReturn(merchant);
        Mockito.when(merchantRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchant));
        Mockito.when(merchantRepository.save(any(Merchant.class))).thenReturn(Mono.just(merchant));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(merchantMapper.toMerchantOutputDtoWithCreator(any(Merchant.class), any(UserOutputDto.class))).thenReturn(merchantOutputDto);
        Mono<MerchantOutputDto> result = merchantService.update(UUID.randomUUID(), merchantInputDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getCreator().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getCreator().getFirstName().equals("firstName") &&
                            resultDto.getCreator().getLastName().equals("lastName") &&
                            resultDto.getCreator().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getCompanyId().equals("company id") &&
                            resultDto.getCompanyName().equals("company name") &&
                            resultDto.getPhoneNumber().equals("phone number") &&
                            resultDto.getEmail().equals("email") &&
                            resultDto.getFilled().equals(true) &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(merchantMapper).toMerchant(any(MerchantInputDto.class));
        Mockito.verify(merchantRepository).findById(any(UUID.class));
        Mockito.verify(merchantRepository).save(any(Merchant.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(merchantMapper).toMerchantOutputDtoWithCreator(any(Merchant.class), any(UserOutputDto.class));
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionIfMerchantNotExists() {
        Mockito.when(merchantMapper.toMerchant(any(MerchantInputDto.class))).thenReturn(merchant);
        Mockito.when(merchantRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<MerchantOutputDto> result = merchantService.update(UUID.randomUUID(), merchantInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Merchant not exists"))
                .verify();
        Mockito.verify(merchantMapper).toMerchant(any(MerchantInputDto.class));
        Mockito.verify(merchantRepository).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnMerchantOutputDto() {
        Mockito.when(merchantRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchant));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(merchantMapper.toMerchantOutputDtoWithCreator(any(Merchant.class), any(UserOutputDto.class))).thenReturn(merchantOutputDto);
        Mono<MerchantOutputDto> result = merchantService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getCreator().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getCreator().getFirstName().equals("firstName") &&
                            resultDto.getCreator().getLastName().equals("lastName") &&
                            resultDto.getCreator().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getCompanyId().equals("company id") &&
                            resultDto.getCompanyName().equals("company name") &&
                            resultDto.getPhoneNumber().equals("phone number") &&
                            resultDto.getEmail().equals("email") &&
                            resultDto.getFilled().equals(true) &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(merchantRepository).findById(any(UUID.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(merchantMapper).toMerchantOutputDtoWithCreator(any(Merchant.class), any(UserOutputDto.class));
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionIfMerchantNotExists() {
        Mockito.when(merchantRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<MerchantOutputDto> result = merchantService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Merchant not exists"))
                .verify();
        Mockito.verify(merchantRepository).findById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldReturnUUID() {
        Mockito.when(merchantRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.just(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")));

        Mono<UUID> result = merchantService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(uuid -> uuid.equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")))
                .expectComplete()
                .verify();
        Mockito.verify(merchantRepository).updateStatusToDeletedById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldThrowEntityNotFoundExceptionIfMerchantNotExists() {
        Mockito.when(merchantRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<UUID> result = merchantService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Merchant not exists"))
                .verify();
        Mockito.verify(merchantRepository).updateStatusToDeletedById(any(UUID.class));
    }

}
