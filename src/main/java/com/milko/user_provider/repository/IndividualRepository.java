package com.milko.user_provider.repository;

import com.milko.user_provider.model.Individual;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IndividualRepository extends R2dbcRepository  <Individual, UUID> {
    @Query("SELECT i.* FROM person.individuals i JOIN person.users u ON i.user_id = u.id WHERE u.auth_service_id = :id")
    Mono<Individual> findByAuthServiceId(UUID id);

    @Query("UPDATE person.individuals SET status = 'DELETED' WHERE id = :id RETURNING id")
    Mono<UUID> updateStatusToDeletedById(UUID id);
}
