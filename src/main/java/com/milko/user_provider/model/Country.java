package com.milko.user_provider.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "person.countries")
public class Country {
    @Id
    private Integer id;
    @Column(value = "created")
    private LocalDateTime created;
    @Column(value = "updated")
    private LocalDateTime updated;
    @Column(value = "name")
    private String name;
    @Column(value = "alpha2")
    private String alpha2;
    @Column(value = "alpha3")
    private String alpha3;
    @Column(value = "status")
    private Status status;
}
