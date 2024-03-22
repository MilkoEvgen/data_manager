package com.milko.data_manager;

import com.milko.data_manager.dto.input.AddressInputDto;
import com.milko.data_manager.dto.output.AddressOutputDto;
import com.milko.data_manager.dto.output.CountryOutputDto;
import com.milko.data_manager.exceptions.EntityNotFoundException;
import com.milko.data_manager.mapper.AddressMapper;
import com.milko.data_manager.mapper.CountryMapper;
import com.milko.data_manager.model.Address;
import com.milko.data_manager.model.Country;
import com.milko.data_manager.model.Status;
import com.milko.data_manager.repository.AddressRepository;
import com.milko.data_manager.repository.CountryRepository;
import com.milko.data_manager.service.impl.AddressServiceImpl;
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
public class AddressServiceImplTest {
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CountryMapper countryMapper;
    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private AddressInputDto addressInputDto;
    private Address address;
    private AddressOutputDto addressOutputDto;
    private Country country;
    private CountryOutputDto countryOutputDto;

    @BeforeEach
    public void init(){
        country = Country.builder()
                .id(1)
                .name("Australia")
                .alpha2("AU")
                .alpha3("AUS")
                .status(Status.ACTIVE)
                .build();
        countryOutputDto = CountryOutputDto.builder()
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
        addressOutputDto = AddressOutputDto.builder()
                .country(countryOutputDto)
                .address("address")
                .zipCode("zipCode")
                .state("state")
                .build();

    }

    @Test
    public void createShouldReturnAddressOutputDto(){
        Mockito.when(addressMapper.toAddress(any(AddressInputDto.class))).thenReturn(address);
        Mockito.when(countryRepository.findById(any(Integer.class))).thenReturn(Mono.just(country));
        Mockito.when(countryMapper.toCountryOutputDto(any(Country.class))).thenReturn(countryOutputDto);
        Mockito.when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address));
        Mockito.when(addressMapper.toAddressOutputDtoWithCountry(any(Address.class), any(CountryOutputDto.class))).thenReturn(addressOutputDto);
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
        Mockito.verify(addressMapper).toAddress(any(AddressInputDto.class));
        Mockito.verify(countryRepository).findById(any(Integer.class));
        Mockito.verify(countryMapper).toCountryOutputDto(any(Country.class));
        Mockito.verify(addressRepository).save(any(Address.class));
        Mockito.verify(addressMapper).toAddressOutputDtoWithCountry(any(Address.class), any(CountryOutputDto.class));
    }

    @Test
    public void createShouldThrowExceptionIfCountryNotExists(){
        Mockito.when(addressMapper.toAddress(any(AddressInputDto.class))).thenReturn(address);
        Mockito.when(countryRepository.findById(any(Integer.class))).thenReturn(Mono.empty());
        Mono<AddressOutputDto> result = addressService.create(addressInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Country not exists"))
                .verify();
        Mockito.verify(addressMapper).toAddress(any(AddressInputDto.class));
        Mockito.verify(countryRepository).findById(any(Integer.class));
    }

    @Test
    public void updateShouldReturnAddressOutputDto(){
        Mockito.when(addressMapper.toAddress(any(AddressInputDto.class))).thenReturn(address);
        Mockito.when(addressRepository.findById(any(UUID.class))).thenReturn(Mono.just(address));
        Mockito.when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address));
        Mockito.when(countryRepository.findById(any(Integer.class))).thenReturn(Mono.just(country));
        Mockito.when(addressMapper.toAddressOutputDtoWithCountry(any(Address.class), any(CountryOutputDto.class))).thenReturn(addressOutputDto);
        Mockito.when(countryMapper.toCountryOutputDto(any(Country.class))).thenReturn(countryOutputDto);
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
        Mockito.verify(addressMapper).toAddress(any(AddressInputDto.class));
        Mockito.verify(addressRepository).findById(any(UUID.class));
        Mockito.verify(addressRepository).save(any(Address.class));
        Mockito.verify(countryRepository).findById(any(Integer.class));
        Mockito.verify(addressMapper).toAddressOutputDtoWithCountry(any(Address.class), any(CountryOutputDto.class));
        Mockito.verify(countryMapper).toCountryOutputDto(any(Country.class));
    }

    @Test
    public void updateShouldThrowExceptionIfAddressNotExists(){
        Mockito.when(addressMapper.toAddress(any(AddressInputDto.class))).thenReturn(address);
        Mockito.when(addressRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<AddressOutputDto> result = addressService.update(UUID.randomUUID(), addressInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Address not exists"))
                .verify();
        Mockito.verify(addressMapper).toAddress(any(AddressInputDto.class));
        Mockito.verify(addressRepository).findById(any(UUID.class));
    }

    @Test
    public void findByIdShouldReturnAddressOutputDto(){
        Mockito.when(addressRepository.findById(any(UUID.class))).thenReturn(Mono.just(address));
        Mockito.when(countryRepository.findById(any(Integer.class))).thenReturn(Mono.just(country));
        Mockito.when(addressMapper.toAddressOutputDtoWithCountry(any(Address.class), any(CountryOutputDto.class))).thenReturn(addressOutputDto);
        Mockito.when(countryMapper.toCountryOutputDto(any(Country.class))).thenReturn(countryOutputDto);
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
        Mockito.verify(addressMapper).toAddressOutputDtoWithCountry(any(Address.class), any(CountryOutputDto.class));
        Mockito.verify(countryMapper).toCountryOutputDto(any(Country.class));
    }

    @Test
    public void findByIdShouldThrowExceptionIfAddressNotExists(){
        Mockito.when(addressRepository.findById(any(UUID.class))).thenReturn(Mono.empty());
        Mono<AddressOutputDto> result = addressService.update(UUID.randomUUID(), addressInputDto);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().contains("Address not exists"))
                .verify();
        Mockito.verify(addressRepository).findById(any(UUID.class));
    }
}
