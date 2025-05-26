package ru.edmebank.clients.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "client_security_info")
@Entity
@Getter
@Setter
public class ClientSecurityInfo {

    @Id
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private Client client;

    @Column(nullable = false)
    private Boolean checked;

    private LocalDateTime date;

    @Column(length = 500)
    private String comment;
}
