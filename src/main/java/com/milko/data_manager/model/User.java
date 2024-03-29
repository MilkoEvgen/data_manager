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
@Table(name = "person.users")
public class User implements Persistable<UUID> {
    @Id
    private UUID id;
    @Column(value = "auth_service_id")
    private UUID authServiceId;
    @Column(value = "created")
    private LocalDateTime created;
    @Column(value = "updated")
    private LocalDateTime updated;
    @Column(value = "first_name")
    private String firstName;
    @Column(value = "last_name")
    private String lastName;
    @Column(value = "verified_at")
    private LocalDateTime verifiedAt;
    @Column(value = "archived_at")
    private LocalDateTime archivedAt;
    @Column(value = "status")
    private Status status;
    @Column(value = "address_id")
    private UUID addressId;

    @Override
    public boolean isNew() {
        return this.id == null;
    }
}
