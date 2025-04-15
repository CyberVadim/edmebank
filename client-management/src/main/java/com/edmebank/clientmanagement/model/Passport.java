package com.edmebank.clientmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "client_passports")
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false, referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_passport_client"))
    private Client client;

    private String documentPath;

    private String passportNumber;

    private LocalDate passportIssueDate;

    private LocalDate uploadedAt = LocalDate.now();
}
