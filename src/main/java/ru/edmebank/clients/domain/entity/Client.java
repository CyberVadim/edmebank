package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import ru.edmebank.contracts.enums.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "clients")
@Entity
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 100)
    private String middleName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private Integer maritalStatusId;
    private Integer employmentId;

    private LocalDateTime createdAt;

    @Column(nullable = false, length = 12)
    private String inn;

    @Column(nullable = false, length = 14)
    private String snils;
    
    @Column(nullable = false)
    private Boolean securityChecked;

    private LocalDateTime securityDate;

    @Column(length = 500)
    private String securityComment;

    @Column(precision = 20, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "has_marriage_contract", nullable = false)
    private Boolean marriageContract;

}
