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
@Table(name = "person.profile_history")
public class ProfileHistory {
    @Id
    private UUID id;
    @Column(value = "created")
    private LocalDateTime created;
    @Column(value = "user_id")
    private UUID userId;
    @Column(value = "profile_type")
    private ProfileType profileType;
    @Column(value = "reason")
    private String reason;
    @Column(value = "comment")
    private String comment;
    @Column(value = "changed_values")
    private String changedValues;
}
