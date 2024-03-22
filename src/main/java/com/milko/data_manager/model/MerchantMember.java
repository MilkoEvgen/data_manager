package com.milko.data_manager.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
@Table(name = "person.merchant_members")
public class MerchantMember implements Persistable<UUID> {
    @Id
    private UUID id;
    @Column(value = "user_id")
    private UUID userId;
    @Column(value = "created")
    private LocalDateTime created;
    @Column(value = "updated")
    private LocalDateTime updated;
    @Column(value = "merchant_id")
    private UUID merchantId;
    @Column(value = "member_role")
    private String memberRole;
    @Column(value = "status")
    private Status status;

    @Override
    public boolean isNew() {
        return this.id == null;
    }
}
