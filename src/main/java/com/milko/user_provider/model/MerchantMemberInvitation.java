package com.milko.user_provider.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
@Table(name = "person.merchant_members_invitations")
public class MerchantMemberInvitation {
    @Id
    private UUID id;
    @Column(value = "created")
    private LocalDateTime created;
    @Column(value = "expires")
    private LocalDateTime expires;
    @Column(value = "merchant_id")
    private UUID merchantId;
    @Column(value = "first_name")
    private String firstName;
    @Column(value = "last_name")
    private String lastName;
    @Column(value = "email")
    private String email;
    @Column(value = "status")
    private Status status;
}
