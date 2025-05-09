package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "client_security_info")
@Entity
@Data
public class ClientSecurityInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "securityInfo")
    private Client client;

    @Column(nullable = false)
    private Boolean checked;

    private LocalDateTime date;

    @Column(length = 500)
    private String comment;
}
