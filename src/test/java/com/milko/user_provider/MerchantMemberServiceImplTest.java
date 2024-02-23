package com.milko.user_provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milko.user_provider.dto.input.MerchantMemberInputDto;
import com.milko.user_provider.dto.input.ProfileHistoryInputDto;
import com.milko.user_provider.dto.output.MerchantMemberOutputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.model.MerchantMember;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.repository.MerchantMemberRepository;
import com.milko.user_provider.service.MerchantService;
import com.milko.user_provider.service.ProfileHistoryService;
import com.milko.user_provider.service.UserService;
import com.milko.user_provider.service.impl.MerchantMemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class MerchantMemberServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private MerchantService merchantService;
    @Mock
    private ProfileHistoryService profileHistoryService;
    @Mock
    private MerchantMemberRepository memberRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private MerchantMemberServiceImpl merchantMemberService;

    private MerchantMemberInputDto merchantMemberInputDto;
    private UserOutputDto userOutputDto;
    private MerchantOutputDto merchantOutputDto;
    private MerchantMember merchantMember;

    @BeforeEach
    public void init(){
        merchantMemberInputDto = MerchantMemberInputDto.builder()
                .userId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .merchantId(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .build();
        userOutputDto = UserOutputDto.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .secretKey("secretKey")
                .firstName("firstName")
                .lastName("lastName")
                .status(Status.ACTIVE)
                .build();
        merchantOutputDto = MerchantOutputDto.builder()
                .id(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .creator(userOutputDto)
                .companyName("companyName")
                .companyId("companyId")
                .email("email")
                .phoneNumber("phoneNumber")
                .status(Status.ACTIVE)
                .build();
        merchantMember = MerchantMember.builder()
                .id(UUID.fromString("2275dd2a-cb0b-4c94-b16e-bc5627c25624"))
                .merchantId(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .userId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .memberRole("role")
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    public void createShouldReturnMerchantMemberOutputDto(){
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(merchantService.findById(any(UUID.class))).thenReturn(Mono.just(merchantOutputDto));
        Mockito.when(memberRepository.save(any(MerchantMember.class))).thenReturn(Mono.just(merchantMember));
        Mono<MerchantMemberOutputDto> result = merchantMemberService.create(merchantMemberInputDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("2275dd2a-cb0b-4c94-b16e-bc5627c25624")) &&
                            resultDto.getMerchant().getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getMerchant().getCompanyName().equals("companyName") &&
                            resultDto.getMerchant().getCompanyId().equals("companyId") &&
                            resultDto.getMerchant().getEmail().equals("email") &&
                            resultDto.getMerchant().getPhoneNumber().equals("phoneNumber") &&
                            resultDto.getMerchant().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getUser().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getUser().getSecretKey().equals("secretKey") &&
                            resultDto.getUser().getFirstName().equals("firstName") &&
                            resultDto.getUser().getLastName().equals("lastName") &&
                            resultDto.getUser().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getMemberRole().equals("role") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(merchantService).findById(any(UUID.class));
        Mockito.verify(memberRepository).save(any(MerchantMember.class));
    }

    @Test
    public void createShouldThrowResponseStatusExceptionIfUserNotExists(){
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<MerchantMemberOutputDto> result = merchantMemberService.create(merchantMemberInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("User not exists"))
                .verify();
        Mockito.verify(userService).findById(any(UUID.class));
    }

    @Test
    public void updateShouldReturnMerchantMemberOutputDto(){
        Mockito.when(memberRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchantMember));
        Mockito.when(profileHistoryService.create(any(ProfileHistoryInputDto.class))).thenReturn(Mono.empty());
        Mockito.when(memberRepository.save(any(MerchantMember.class))).thenReturn(Mono.just(merchantMember));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(merchantService.findById(any(UUID.class))).thenReturn(Mono.just(merchantOutputDto));
        Mono<MerchantMemberOutputDto> result = merchantMemberService.update(UUID.randomUUID(), merchantMemberInputDto, "reason", "comment");
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("2275dd2a-cb0b-4c94-b16e-bc5627c25624")) &&
                            resultDto.getMerchant().getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getMerchant().getCompanyName().equals("companyName") &&
                            resultDto.getMerchant().getCompanyId().equals("companyId") &&
                            resultDto.getMerchant().getEmail().equals("email") &&
                            resultDto.getMerchant().getPhoneNumber().equals("phoneNumber") &&
                            resultDto.getMerchant().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getUser().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getUser().getSecretKey().equals("secretKey") &&
                            resultDto.getUser().getFirstName().equals("firstName") &&
                            resultDto.getUser().getLastName().equals("lastName") &&
                            resultDto.getUser().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getMemberRole().equals("role") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(memberRepository).findById(any(UUID.class));
        Mockito.verify(profileHistoryService).create(any(ProfileHistoryInputDto.class));
        Mockito.verify(memberRepository).save(any(MerchantMember.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(merchantService).findById(any(UUID.class));
    }

    @Test
    public void updateShouldThrowResponseStatusExceptionIfMerchantMemberNotExists(){
        Mockito.when(memberRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<MerchantMemberOutputDto> result = merchantMemberService.update(UUID.randomUUID(), merchantMemberInputDto, "reason", "comment");
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Merchant member not exists"))
                .verify();
        Mockito.verify(memberRepository).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnMerchantMemberOutputDto(){
        Mockito.when(memberRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchantMember));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(merchantService.findById(any(UUID.class))).thenReturn(Mono.just(merchantOutputDto));
        Mono<MerchantMemberOutputDto> result = merchantMemberService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getId().equals(UUID.fromString("2275dd2a-cb0b-4c94-b16e-bc5627c25624")) &&
                            resultDto.getMerchant().getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getMerchant().getCompanyName().equals("companyName") &&
                            resultDto.getMerchant().getCompanyId().equals("companyId") &&
                            resultDto.getMerchant().getEmail().equals("email") &&
                            resultDto.getMerchant().getPhoneNumber().equals("phoneNumber") &&
                            resultDto.getMerchant().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getUser().getId().equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")) &&
                            resultDto.getUser().getSecretKey().equals("secretKey") &&
                            resultDto.getUser().getFirstName().equals("firstName") &&
                            resultDto.getUser().getLastName().equals("lastName") &&
                            resultDto.getUser().getStatus().equals(Status.ACTIVE) &&
                            resultDto.getMemberRole().equals("role") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(memberRepository).findById(any(UUID.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(merchantService).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldThrowResponseStatusExceptionIfMerchantMemberNotExists(){
        Mockito.when(memberRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<MerchantMemberOutputDto> result = merchantMemberService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Merchant member not exists"))
                .verify();
        Mockito.verify(memberRepository).findById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldReturnInteger(){
        Mockito.when(memberRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.just(1));
        Mono<Integer> result = merchantMemberService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(integer -> integer == 1)
                .expectComplete()
                .verify();
        Mockito.verify(memberRepository).updateStatusToDeletedById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldThrowResponseStatusExceptionIfMerchantMemberNotExists(){
        Mockito.when(memberRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.just(0));
        Mono<Integer> result = merchantMemberService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Merchant member not exists"))
                .verify();
        Mockito.verify(memberRepository).updateStatusToDeletedById(any(UUID.class));
    }


}
