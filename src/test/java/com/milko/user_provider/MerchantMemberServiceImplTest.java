package com.milko.user_provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milko.user_provider.dto.input.RegisterMerchantMemberInputDto;
import com.milko.user_provider.dto.input.UpdateMerchantMemberDto;
import com.milko.user_provider.dto.output.MerchantMemberOutputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.dto.output.UserOutputDto;
import com.milko.user_provider.exceptions.EntityNotFoundException;
import com.milko.user_provider.mapper.MerchantMemberMapper;
import com.milko.user_provider.model.MerchantMember;
import com.milko.user_provider.model.ProfileHistory;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.model.User;
import com.milko.user_provider.repository.MerchantMemberRepository;
import com.milko.user_provider.repository.ProfileHistoryRepository;
import com.milko.user_provider.repository.UserRepository;
import com.milko.user_provider.service.MerchantService;
import com.milko.user_provider.service.UserService;
import com.milko.user_provider.service.impl.MerchantMemberServiceImpl;
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
public class MerchantMemberServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MerchantService merchantService;
    @Mock
    private ProfileHistoryRepository profileHistoryRepository;
    @Mock
    private MerchantMemberRepository memberRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private MerchantMemberMapper memberMapper;
    @InjectMocks
    private MerchantMemberServiceImpl merchantMemberService;

    private MerchantMemberOutputDto memberOutputDto;
    private UserOutputDto userOutputDto;
    private MerchantOutputDto merchantOutputDto;
    private MerchantMember merchantMember;
    private User user;
    private RegisterMerchantMemberInputDto registerInputDto;
    private UpdateMerchantMemberDto updateMerchantMemberDto;

    @BeforeEach
    public void init(){
        user = User.builder()
                .id(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
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
        memberOutputDto = MerchantMemberOutputDto.builder()
                .id(UUID.fromString("2275dd2a-cb0b-4c94-b16e-bc5627c25624"))
                .merchant(merchantOutputDto)
                .user(userOutputDto)
                .memberRole("role")
                .status(Status.ACTIVE)
                .build();
        registerInputDto = RegisterMerchantMemberInputDto.builder()
                .secretKey("secretKey")
                .firstName("firstName")
                .lastName("lastName")
                .addressId(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469"))
                .merchantId(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .memberRole("role")
                .build();
        updateMerchantMemberDto = UpdateMerchantMemberDto.builder()
                .merchantMemberId(UUID.fromString("2275dd2a-cb0b-4c94-b16e-bc5627c25624"))
                .merchantMember(MerchantMember.builder().build())
                .reason("reason")
                .comment("comment")
                .build();
    }

    @Test
    public void createShouldReturnMerchantMemberOutputDto(){
        Mockito.when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        Mockito.when(memberRepository.save(any(MerchantMember.class))).thenReturn(Mono.just(merchantMember));
        Mockito.when(profileHistoryRepository.save(any(ProfileHistory.class))).thenReturn(Mono.empty());
        Mockito.when(merchantService.findById(any(UUID.class))).thenReturn(Mono.just(merchantOutputDto));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(memberMapper.toMemberOutputDtoWithUserAndMerchant(any(MerchantMember.class), any(UserOutputDto.class), any(MerchantOutputDto.class))).thenReturn(memberOutputDto);
        Mono<MerchantMemberOutputDto> result = merchantMemberService.create(registerInputDto);
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
        Mockito.verify(userRepository).save(any(User.class));
        Mockito.verify(memberRepository).save(any(MerchantMember.class));
        Mockito.verify(profileHistoryRepository).save(any(ProfileHistory.class));
        Mockito.verify(merchantService).findById(any(UUID.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(memberMapper).toMemberOutputDtoWithUserAndMerchant(any(MerchantMember.class), any(UserOutputDto.class), any(MerchantOutputDto.class));
    }

    @Test
    public void updateShouldReturnMerchantMemberOutputDto(){
        Mockito.when(memberRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchantMember));
        Mockito.when(profileHistoryRepository.save(any(ProfileHistory.class))).thenReturn(Mono.empty());
        Mockito.when(memberRepository.save(any(MerchantMember.class))).thenReturn(Mono.just(merchantMember));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(merchantService.findById(any(UUID.class))).thenReturn(Mono.just(merchantOutputDto));
        Mockito.when(memberMapper.toMemberOutputDtoWithUserAndMerchant(any(MerchantMember.class), any(UserOutputDto.class), any(MerchantOutputDto.class))).thenReturn(memberOutputDto);
        Mono<MerchantMemberOutputDto> result = merchantMemberService.update(updateMerchantMemberDto);
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
        Mockito.verify(profileHistoryRepository).save(any(ProfileHistory.class));
        Mockito.verify(memberRepository).save(any(MerchantMember.class));
        Mockito.verify(userService).findById(any(UUID.class));
        Mockito.verify(merchantService).findById(any(UUID.class));
        Mockito.verify(memberMapper).toMemberOutputDtoWithUserAndMerchant(any(MerchantMember.class), any(UserOutputDto.class), any(MerchantOutputDto.class));
    }

    @Test
    public void updateShouldThrowEntityNotFoundExceptionIfMerchantMemberNotExists(){
        Mockito.when(memberRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<MerchantMemberOutputDto> result = merchantMemberService.update(updateMerchantMemberDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Merchant member not exists"))
                .verify();
        Mockito.verify(memberRepository).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnMerchantMemberOutputDto(){
        Mockito.when(memberRepository.findById(any(UUID.class))).thenReturn(Mono.just(merchantMember));
        Mockito.when(userService.findById(any(UUID.class))).thenReturn(Mono.just(userOutputDto));
        Mockito.when(merchantService.findById(any(UUID.class))).thenReturn(Mono.just(merchantOutputDto));
        Mockito.when(memberMapper.toMemberOutputDtoWithUserAndMerchant(any(MerchantMember.class), any(UserOutputDto.class), any(MerchantOutputDto.class))).thenReturn(memberOutputDto);
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
        Mockito.verify(memberMapper).toMemberOutputDtoWithUserAndMerchant(any(MerchantMember.class), any(UserOutputDto.class), any(MerchantOutputDto.class));
    }

    @Test
    public void findByIdShouldThrowEntityNotFoundExceptionIfMerchantMemberNotExists(){
        Mockito.when(memberRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<MerchantMemberOutputDto> result = merchantMemberService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Merchant member not exists"))
                .verify();
        Mockito.verify(memberRepository).findById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldReturnUUID(){
        Mockito.when(memberRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.just(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")));
        Mono<UUID> result = merchantMemberService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(uuid -> uuid.equals(UUID.fromString("b52db198-e5bd-4768-9735-a2e862d6c469")))
                .expectComplete()
                .verify();
        Mockito.verify(memberRepository).updateStatusToDeletedById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldThrowEntityNotFoundExceptionIfMerchantMemberNotExists(){
        Mockito.when(memberRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<UUID> result = merchantMemberService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Merchant member not exists"))
                .verify();
        Mockito.verify(memberRepository).updateStatusToDeletedById(any(UUID.class));
    }

}
