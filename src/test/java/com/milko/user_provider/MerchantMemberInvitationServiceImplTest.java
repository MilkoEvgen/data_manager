package com.milko.user_provider;

import com.milko.user_provider.dto.input.MerchantMemberInvitationInputDto;
import com.milko.user_provider.dto.output.MerchantMemberInvitationOutputDto;
import com.milko.user_provider.dto.output.MerchantOutputDto;
import com.milko.user_provider.exceptions.EntityNotFoundException;
import com.milko.user_provider.mapper.MerchantMemberInvitationMapper;
import com.milko.user_provider.model.MerchantMemberInvitation;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.repository.MerchantMemberInvitationRepository;
import com.milko.user_provider.service.MerchantService;
import com.milko.user_provider.service.impl.MerchantMemberInvitationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class MerchantMemberInvitationServiceImplTest {
    @Mock
    private MerchantMemberInvitationRepository invitationRepository;
    @Mock
    private MerchantService merchantService;
    @Mock
    private MerchantMemberInvitationMapper invitationMapper;
    @InjectMocks
    private MerchantMemberInvitationServiceImpl invitationService;

    private MerchantMemberInvitationInputDto invitationInputDto;
    private MerchantMemberInvitationOutputDto invitationOutputDto;
    private MerchantMemberInvitation memberInvitation;
    private MerchantOutputDto merchantOutputDto;

    @BeforeEach
    public void init() {
        merchantOutputDto = MerchantOutputDto.builder()
                .id(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .companyName("company name")
                .companyId("company id")
                .email("email")
                .phoneNumber("phone number")
                .build();
        invitationInputDto = MerchantMemberInvitationInputDto.builder()
                .validForDays(5L)
                .merchantId(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .firstName("first name")
                .lastName("last name")
                .email("email")
                .build();
        memberInvitation = MerchantMemberInvitation.builder()
                .merchantId(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949"))
                .firstName("first name")
                .lastName("last name")
                .email("email")
                .build();
        invitationOutputDto = MerchantMemberInvitationOutputDto.builder()
                .merchant(merchantOutputDto)
                .firstName("first name")
                .lastName("last name")
                .email("email")
                .status(Status.ACTIVE)
                .build();

    }

    @Test
    public void createReturnMerchantMemberInvitationOutputDto() {
        Mockito.when(invitationMapper.toInvitation(any(MerchantMemberInvitationInputDto.class))).thenReturn(memberInvitation);
        Mockito.when(merchantService.findById(any(UUID.class))).thenReturn(Mono.just(merchantOutputDto));
        Mockito.when(invitationRepository.save(any(MerchantMemberInvitation.class))).thenReturn(Mono.just(memberInvitation));
        Mockito.when(invitationMapper.toInvitationDtoWithMerchant(any(MerchantMemberInvitation.class), any(MerchantOutputDto.class))).thenReturn(invitationOutputDto);
        Mono<MerchantMemberInvitationOutputDto> result = invitationService.create(invitationInputDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getMerchant().getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getMerchant().getCompanyName().equals("company name") &&
                            resultDto.getMerchant().getCompanyId().equals("company id") &&
                            resultDto.getMerchant().getEmail().equals("email") &&
                            resultDto.getMerchant().getPhoneNumber().equals("phone number") &&
                            resultDto.getFirstName().equals("first name") &&
                            resultDto.getLastName().equals("last name") &&
                            resultDto.getEmail().equals("email") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(invitationMapper).toInvitation(any(MerchantMemberInvitationInputDto.class));
        Mockito.verify(merchantService).findById(any(UUID.class));
        Mockito.verify(invitationRepository).save(any(MerchantMemberInvitation.class));
        Mockito.verify(invitationMapper).toInvitationDtoWithMerchant(any(MerchantMemberInvitation.class), any(MerchantOutputDto.class));
    }

    @Test
    public void createThrowExceptionIfMerchantNotExist() {
        Mockito.when(invitationMapper.toInvitation(any(MerchantMemberInvitationInputDto.class))).thenReturn(memberInvitation);
        Mockito.when(merchantService.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<MerchantMemberInvitationOutputDto> result = invitationService.create(invitationInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Merchant not exists"))
                .verify();
        Mockito.verify(invitationMapper).toInvitation(any(MerchantMemberInvitationInputDto.class));
        Mockito.verify(merchantService).findById(any(UUID.class));
    }

    @Test
    public void findAllByMerchantIdReturnMerchantMemberInvitationOutputDto() {
        Mockito.when(invitationRepository.findAllByMerchantId(any(UUID.class))).thenReturn(Flux.just(memberInvitation));
        Mockito.when(invitationMapper.toInvitationDtoWithMerchant(any(MerchantMemberInvitation.class), Mockito.any())).thenReturn(invitationOutputDto);
        Flux<MerchantMemberInvitationOutputDto> result = invitationService.findAllByMerchantId(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getMerchant().getId().equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")) &&
                            resultDto.getMerchant().getCompanyName().equals("company name") &&
                            resultDto.getMerchant().getCompanyId().equals("company id") &&
                            resultDto.getMerchant().getEmail().equals("email") &&
                            resultDto.getMerchant().getPhoneNumber().equals("phone number") &&
                            resultDto.getFirstName().equals("first name") &&
                            resultDto.getLastName().equals("last name") &&
                            resultDto.getEmail().equals("email") &&
                            resultDto.getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(invitationRepository).findAllByMerchantId(any(UUID.class));
        Mockito.verify(invitationMapper).toInvitationDtoWithMerchant(any(MerchantMemberInvitation.class), Mockito.any());
    }

    @Test
    public void deleteByIdShouldReturnUUID() {
        Mockito.when(invitationRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.just(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")));

        Mono<UUID> result = invitationService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(uuid -> uuid.equals(UUID.fromString("15108ff4-0170-4966-a69c-9637953da949")))
                .expectComplete()
                .verify();
        Mockito.verify(invitationRepository).updateStatusToDeletedById(any(UUID.class));
    }

    @Test
    public void deleteByIdShouldThrowEntityNotFoundExceptionIfMerchantNotExists() {
        Mockito.when(invitationRepository.updateStatusToDeletedById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<UUID> result = invitationService.deleteById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Merchant member invitation not exists"))
                .verify();
        Mockito.verify(invitationRepository).updateStatusToDeletedById(any(UUID.class));
    }
}
