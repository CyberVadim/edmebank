package com.edmebank.clientmanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(length = 100, nullable = false)
    private String firstName;

    @Column(length = 100, nullable = false)
    private String lastName;

    @Column(length = 100)
    private String middleName;

    @Column(name = "date_of_birth", columnDefinition = "DATE", nullable = false)
    private LocalDate dateOfBirth;

    @Column(unique = true, nullable = false, length = 10)
    private String passportNumber;

    @Column(length = 4)
    private String passportSeries;

    @Column(length = 255)
    private String passportIssuedBy;

    @Column(name = "passport_issue_date", columnDefinition = "DATE")
    private LocalDate passportIssueDate;

    @Column(length = 500)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(length = 12)
    private String inn;

    @Column(length = 14)
    private String snils;

    @Column(nullable = false)
    private boolean amlChecked = false;

    @Column(name = "passport_expiry_date", columnDefinition = "DATE")
    private LocalDate passportExpiryDate;

    @PrePersist
    @PreUpdate
    public void calculatePassportExpiryDate() {
        if (dateOfBirth == null) {
            return;
        }
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();

        if (age < 20) {
            passportExpiryDate = dateOfBirth.plusYears(20);
        } else if (age < 45) {
            passportExpiryDate = dateOfBirth.plusYears(45);
        } else {
            passportExpiryDate = null;
        }
    }
}
