package com.milko.data_manager.repository;

import com.milko.data_manager.model.Address;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface AddressRepository extends R2dbcRepository<Address, UUID> {

}
