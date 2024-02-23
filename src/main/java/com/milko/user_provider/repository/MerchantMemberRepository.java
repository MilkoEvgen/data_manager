package com.milko.user_provider.repository;

import com.milko.user_provider.model.MerchantMember;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantMemberRepository extends R2dbcRepository<MerchantMember, UUID> {
    @Query("UPDATE person.merchant_members SET status = 'DELETED' WHERE id = :id")
    Mono<Integer> updateStatusToDeletedById(UUID id);
}
