package com.milko.user_provider.repository;

import com.milko.user_provider.model.Merchant;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantRepository extends R2dbcRepository<Merchant, UUID> {
    @Query("UPDATE person.merchants SET status = 'DELETED' WHERE id = :id")
    Mono<Integer> updateStatusToDeletedById(UUID id);
}
