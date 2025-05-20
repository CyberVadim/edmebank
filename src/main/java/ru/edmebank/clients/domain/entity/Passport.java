package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "passports")
@Entity
@Getter
@Setter
public class Passport {

    @Id
    @Column(nullable = false)
    private UUID clientId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(nullable = false, length = 4)
    private String series;

    @Column(nullable = false, length = 6)
    private String number;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false, length = 500)
    private String issuedBy;

    @Column(nullable = false, length = 7)
    private String departmentCode;

}
