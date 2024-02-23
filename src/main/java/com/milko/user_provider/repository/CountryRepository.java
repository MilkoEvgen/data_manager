package com.milko.user_provider.repository;

import com.milko.user_provider.model.Country;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CountryRepository extends R2dbcRepository<Country, Integer> {
}
