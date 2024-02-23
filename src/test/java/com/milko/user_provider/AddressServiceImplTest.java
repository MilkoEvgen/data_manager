package com.milko.user_provider;

import com.milko.user_provider.dto.input.AddressInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.model.Address;
import com.milko.user_provider.model.Country;
import com.milko.user_provider.model.Status;
import com.milko.user_provider.repository.AddressRepository;
import com.milko.user_provider.repository.CountryRepository;
import com.milko.user_provider.service.impl.AddressServiceImpl;
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
public class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private CountryRepository countryRepository;
    @InjectMocks
    private AddressServiceImpl addressService;

    private AddressInputDto addressInputDto;
    private Address address;
    private Country country;

    @BeforeEach
    public void init(){
        country = Country.builder()
                .id(1)
                .name("Australia")
                .alpha2("AU")
                .alpha3("AUS")
                .status(Status.ACTIVE)
                .build();
        addressInputDto = AddressInputDto.builder()
                .countryId(1)
                .address("address")
                .zipCode("zipCode")
                .state("state")
                .build();
        address = Address.builder()
                .countryId(1)
                .address("address")
                .zipCode("zipCode")
                .state("state")
                .build();
    }

    @Test
    public void createShouldReturnAddressOutputDto(){
        Mockito.when(countryRepository.findById(any(Integer.class))).thenReturn(Mono.just(country));
        Mockito.when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address));
        Mono<AddressOutputDto> result = addressService.create(addressInputDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getAddress().equals("address") &&
                            resultDto.getZipCode().equals("zipCode") &&
                            resultDto.getState().equals("state") &&
                            resultDto.getCountry().getId() == 1 &&
                            resultDto.getCountry().getName().equals("Australia") &&
                            resultDto.getCountry().getAlpha2().equals("AU") &&
                            resultDto.getCountry().getAlpha3().equals("AUS") &&
                            resultDto.getCountry().getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(countryRepository).findById(any(Integer.class));
        Mockito.verify(addressRepository).save(any(Address.class));
    }

    @Test
    public void createShouldThrowResponseStatusExceptionIfCountryNotExists(){
        Mockito.when(countryRepository.findById(any(Integer.class))).thenReturn(Mono.empty());
        Mono<AddressOutputDto> result = addressService.create(addressInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Country not exists"))
                .verify();
        Mockito.verify(countryRepository).findById(any(Integer.class));
    }

    @Test
    public void updateShouldReturnAddressOutputDto(){
        Mockito.when(addressRepository.findById(any(UUID.class))).thenReturn(Mono.just(address));
        Mockito.when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address));
        Mockito.when(countryRepository.findById(any(Integer.class))).thenReturn(Mono.just(country));

        Mono<AddressOutputDto> result = addressService.update(UUID.randomUUID(), addressInputDto);
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getAddress().equals("address") &&
                            resultDto.getZipCode().equals("zipCode") &&
                            resultDto.getState().equals("state") &&
                            resultDto.getCountry().getId() == 1 &&
                            resultDto.getCountry().getName().equals("Australia") &&
                            resultDto.getCountry().getAlpha2().equals("AU") &&
                            resultDto.getCountry().getAlpha3().equals("AUS") &&
                            resultDto.getCountry().getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(addressRepository).findById(any(UUID.class));
        Mockito.verify(addressRepository).save(any(Address.class));
        Mockito.verify(countryRepository).findById(any(Integer.class));
    }

    @Test
    public void updateShouldThrowResponseStatusExceptionIfAddressNotExists(){
        Mockito.when(addressRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<AddressOutputDto> result = addressService.update(UUID.randomUUID(), addressInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Address not exists"))
                .verify();
        Mockito.verify(addressRepository).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnAddressOutputDto(){
        Mockito.when(addressRepository.findById(any(UUID.class))).thenReturn(Mono.just(address));
        Mockito.when(countryRepository.findById(any(Integer.class))).thenReturn(Mono.just(country));

        Mono<AddressOutputDto> result = addressService.findById(UUID.randomUUID());
        StepVerifier.create(result)
                .expectNextMatches(resultDto -> {
                    return resultDto.getAddress().equals("address") &&
                            resultDto.getZipCode().equals("zipCode") &&
                            resultDto.getState().equals("state") &&
                            resultDto.getCountry().getId() == 1 &&
                            resultDto.getCountry().getName().equals("Australia") &&
                            resultDto.getCountry().getAlpha2().equals("AU") &&
                            resultDto.getCountry().getAlpha3().equals("AUS") &&
                            resultDto.getCountry().getStatus().equals(Status.ACTIVE);
                }).expectComplete()
                .verify();
        Mockito.verify(addressRepository).findById(any(UUID.class));
        Mockito.verify(countryRepository).findById(any(Integer.class));
    }

    @Test
    public void findByIdShouldThrowResponseStatusExceptionIfAddressNotExists(){
        Mockito.when(addressRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<AddressOutputDto> result = addressService.update(UUID.randomUUID(), addressInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException &&
                                ((ResponseStatusException) throwable).getStatusCode().equals(HttpStatus.NOT_FOUND) &&
                                throwable.getMessage().contains("Address not exists"))
                .verify();
        Mockito.verify(addressRepository).findById(any(UUID.class));
    }
}
