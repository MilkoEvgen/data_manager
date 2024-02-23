package com.milko.user_provider.service.impl;

import com.milko.user_provider.dto.input.AddressInputDto;
import com.milko.user_provider.dto.output.AddressOutputDto;
import com.milko.user_provider.dto.output.CountryOutputDto;
import com.milko.user_provider.mapper.AddressMapper;
import com.milko.user_provider.mapper.CountryMapper;
import com.milko.user_provider.model.Address;
import com.milko.user_provider.model.User;
import com.milko.user_provider.repository.AddressRepository;
import com.milko.user_provider.repository.CountryRepository;
import com.milko.user_provider.service.AddressService;
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
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final CountryRepository countryRepository;

    @Override
    public Mono<AddressOutputDto> create(AddressInputDto addressInputDto) {
        log.info("IN AddressService.create(), InputDto = {}", addressInputDto);
        Address address = AddressMapper.map(addressInputDto);
        address.setCreated(LocalDateTime.now());
        address.setUpdated(LocalDateTime.now());
        address.setArchived(LocalDateTime.now());
        return countryRepository.findById(address.getCountryId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Country not exists")))
                .flatMap(country -> {
                            CountryOutputDto countryOutputDto = CountryMapper.map(country);
                            return addressRepository.save(address)
                                    .map(savedAddress -> AddressMapper.map(savedAddress, countryOutputDto));
                        });
    }

    @Override
    public Mono<AddressOutputDto> update(UUID id, AddressInputDto addressInputDto) {
        log.info("IN AddressService.update(),id = {}, InputDto = {}", id, addressInputDto);
        Address address = AddressMapper.map(addressInputDto);
        return addressRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not exists")))
                .flatMap(oldAddress -> addressRepository.save(setNewValuesToOldAddress(address, oldAddress)))
                .flatMap(savedAddress -> countryRepository.findById(savedAddress.getCountryId())
                        .map(country -> AddressMapper.map(savedAddress, CountryMapper.map(country))));
    }

    @Override
    public Mono<AddressOutputDto> findById(UUID id) {
        log.info("IN AddressService.findById(), id = {}", id);
        return addressRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not exists")))
                .flatMap(address -> countryRepository.findById(address.getCountryId())
                .map(country -> AddressMapper.map(address, CountryMapper.map(country))));
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
