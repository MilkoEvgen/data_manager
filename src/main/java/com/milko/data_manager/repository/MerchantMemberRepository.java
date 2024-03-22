package com.milko.data_manager.repository;

import com.milko.data_manager.model.MerchantMember;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantMemberRepository extends R2dbcRepository<MerchantMember, UUID> {
    @Query("UPDATE person.merchant_members SET status = 'DELETED' WHERE id = :id RETURNING id")
    Mono<UUID> updateStatusToDeletedById(UUID id);
}
