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
@Table(name = "person.addresses")
public class Address {
    @Id
    private UUID id;
    @Column(value = "created")
    private LocalDateTime created;
    @Column(value = "updated")
    private LocalDateTime updated;
    @Column(value = "address")
    private String address;
    @Column(value = "zip_code")
    private String zipCode;
    @Column(value = "archived")
    private LocalDateTime archived;
    @Column(value = "city")
    private String city;
    @Column(value = "state")
    private String state;
    @Column(value = "country_id")
    private Integer countryId;
}
