package com.milko.data_manager.repository;

import com.milko.data_manager.model.Country;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CountryRepository extends R2dbcRepository<Country, Integer> {
}
