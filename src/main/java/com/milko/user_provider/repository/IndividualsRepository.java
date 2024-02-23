package com.milko.user_provider.repository;

import com.milko.user_provider.model.Individuals;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IndividualsRepository extends R2dbcRepository  <Individuals, UUID> {
    @Query("UPDATE person.individuals SET status = 'DELETED' WHERE id = :id")
    Mono<Boolean> updateStatusToDeletedById(UUID id);
}
