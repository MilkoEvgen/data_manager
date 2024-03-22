package com.milko.data_manager.repository;

import com.milko.data_manager.model.VerificationStatus;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface VerificationStatusRepository extends R2dbcRepository<VerificationStatus, UUID> {

}
