package com.milko.data_manager.service.impl;

import com.milko.data_manager.dto.input.AddressInputDto;
import com.milko.data_manager.dto.output.AddressOutputDto;
import com.milko.data_manager.dto.output.CountryOutputDto;
import com.milko.data_manager.exceptions.EntityNotFoundException;
import com.milko.data_manager.mapper.AddressMapper;
import com.milko.data_manager.mapper.CountryMapper;
import com.milko.data_manager.model.Address;
import com.milko.data_manager.repository.AddressRepository;
import com.milko.data_manager.repository.CountryRepository;
import com.milko.data_manager.service.AddressService;
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
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final AddressMapper addressMapper;

    @Override
    public Mono<AddressOutputDto> create(AddressInputDto addressInputDto) {
        log.info("IN AddressService.create(), InputDto = {}", addressInputDto);
        Address address = addressMapper.toAddress(addressInputDto);
        address.setCreated(LocalDateTime.now());
        address.setUpdated(LocalDateTime.now());
        address.setArchived(LocalDateTime.now());
        return countryRepository.findById(address.getCountryId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Country not exists")))
                .flatMap(country -> {
                            CountryOutputDto countryOutputDto = countryMapper.toCountryOutputDto(country);
                            return addressRepository.save(address)
                                    .map(savedAddress -> addressMapper.toAddressOutputDtoWithCountry(savedAddress, countryOutputDto));
                        });
    }

    @Override
    public Mono<AddressOutputDto> update(UUID id, AddressInputDto addressInputDto) {
        log.info("IN AddressService.update(),id = {}, InputDto = {}", id, addressInputDto);
        Address address = addressMapper.toAddress(addressInputDto);
        return addressRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Address not exists")))
                .flatMap(oldAddress -> addressRepository.save(setNewValuesToOldAddress(address, oldAddress)))
                .flatMap(savedAddress -> countryRepository.findById(savedAddress.getCountryId())
                        .map(country -> addressMapper.toAddressOutputDtoWithCountry(savedAddress, countryMapper.toCountryOutputDto(country))));
    }

    @Override
    public Mono<AddressOutputDto> findById(UUID id) {
        log.info("IN AddressService.findById(), id = {}", id);
        return addressRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Address not exists")))
                .flatMap(address -> countryRepository.findById(address.getCountryId())
                .map(country -> addressMapper.toAddressOutputDtoWithCountry(address, countryMapper.toCountryOutputDto(country))));
    }

    private Address setNewValuesToOldAddress(Address newAddress, Address oldAddress){
        oldAddress.setUpdated(LocalDateTime.now());

        if (!Objects.equals(newAddress.getCountryId(), null)){
            oldAddress.setCountryId(newAddress.getCountryId());
        }
        if (!Objects.equals(newAddress.getAddress(), null)){
            oldAddress.setAddress(newAddress.getAddress());
        }
        if (!Objects.equals(newAddress.getZipCode(), null)){
            oldAddress.setZipCode(newAddress.getZipCode());
        }
        if (!Objects.equals(newAddress.getCity(), null)){
            oldAddress.setCity(newAddress.getCity());
        }
        if (!Objects.equals(newAddress.getState(), null)){
            oldAddress.setState(newAddress.getState());
        }
        return oldAddress;
    }
}
