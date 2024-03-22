package com.milko.data_manager.repository;

import com.milko.data_manager.model.ProfileHistory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ProfileHistoryRepository extends R2dbcRepository<ProfileHistory, UUID> {

    Flux<ProfileHistory> getAllByUserId(UUID userId);
}
