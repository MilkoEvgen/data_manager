package com.milko.data_manager.repository;

import com.milko.data_manager.model.MerchantMemberInvitation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantMemberInvitationRepository extends R2dbcRepository<MerchantMemberInvitation, UUID> {
    Flux<MerchantMemberInvitation> findAllByMerchantId(UUID merchantId);

    @Query("UPDATE person.merchant_members_invitations SET status = 'DELETED' WHERE id = :id RETURNING id")
    Mono<UUID> updateStatusToDeletedById(UUID id);
}
