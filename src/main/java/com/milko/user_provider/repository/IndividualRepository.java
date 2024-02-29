package com.milko.user_provider.repository;

import com.milko.user_provider.model.Individual;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IndividualRepository extends R2dbcRepository  <Individual, UUID> {
    @Query("UPDATE person.individuals SET status = 'DELETED' WHERE id = :id RETURNING id")
    Mono<UUID> updateStatusToDeletedById(UUID id);
}
