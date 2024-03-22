package com.milko.data_manager.repository;

import com.milko.data_manager.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends R2dbcRepository<User, UUID> {
    @Query("UPDATE person.users SET status = 'DELETED' WHERE id = :id RETURNING id")
    Mono<UUID> updateStatusToDeletedById(UUID id);
}
