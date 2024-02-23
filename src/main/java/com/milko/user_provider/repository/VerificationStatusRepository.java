package com.milko.user_provider.repository;

import com.milko.user_provider.model.VerificationStatus;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VerificationStatusRepository extends R2dbcRepository<VerificationStatus, UUID> {

}
