package com.milko.user_provider.repository;

import com.milko.user_provider.model.Address;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AddressRepository extends R2dbcRepository<Address, UUID> {

}
