package com.milko.data_manager.repository;

import com.milko.data_manager.model.Merchant;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantRepository extends R2dbcRepository<Merchant, UUID> {
    @Query("UPDATE person.merchants SET status = 'DELETED' WHERE id = :id RETURNING id")
    Mono<UUID> updateStatusToDeletedById(UUID id);
}
