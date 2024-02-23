package com.milko.user_provider.repository;

import com.milko.user_provider.model.ProfileHistory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ProfileHistoryRepository extends R2dbcRepository<ProfileHistory, UUID> {

    Flux<ProfileHistory> getAllByProfileId(UUID profileId);
}
