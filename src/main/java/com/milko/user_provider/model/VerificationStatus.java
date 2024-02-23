package com.milko.user_provider.model;

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
@Table(name = "person.verification_statuses")
public class VerificationStatus {
    @Id
    private UUID id;
    @Column(value = "created")
    private LocalDateTime created;
    @Column(value = "updated")
    private LocalDateTime updated;
    @Column(value = "profile_id")
    private UUID profileId;
    @Column(value = "profile_type")
    private String profileType;
    @Column(value = "details")
    private String details;
    @Column(value = "verification_status")
    private StatusOfVerification verificationStatus;
}
