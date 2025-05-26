package ru.edmebank.clients.domain.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.edmebank.contracts.enums.AddressType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address {

    @Id
    private UUID clientId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AddressType type;

    @Column(length = 6)
    private String postalCode;

    @Column(length = 100)
    private String region;

    @Column(length = 100, nullable = false)
    private String city;

    @Column(length = 100)
    private String street;

    @Column(length = 10, nullable = false)
    private String house;

    @Column(length = 10)
    private String apartment;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    public LocalDateTime updatedAt;
}
