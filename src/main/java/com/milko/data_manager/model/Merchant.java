package com.milko.data_manager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "person.merchants")
public class Merchant {
    @Id
    private UUID id;
    @Column(value = "creator_id")
    private UUID creatorId;
    @Column(value = "created")
    private LocalDateTime created;
    @Column(value = "updated")
    private LocalDateTime updated;
    @Column(value = "company_name")
    private String companyName;
    @Column(value = "company_id")
    private String companyId;
    @Column(value = "email")
    private String email;
    @Column(value = "phone_number")
    private String phoneNumber;
    @Column(value = "verified_at")
    private LocalDateTime verifiedAt;
    @Column(value = "archived_at")
    private LocalDateTime archivedAt;
    @Column(value = "status")
    private Status status;
    @Column(value = "filled")
    private Boolean filled;
}
