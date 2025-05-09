package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import ru.edmebank.contracts.enums.EmploymentType;
import ru.edmebank.contracts.enums.Gender;
import ru.edmebank.contracts.enums.MaritalStatus;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaritalStatus maritalStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 12, unique = true)
    private String inn;

    @Column(nullable = false, length = 14, unique = true)
    private String snils;

    @OneToOne
    @JoinColumn(name = "client_security_info_id", referencedColumnName = "id")
    private ClientSecurityInfo securityInfo;

    @Column(precision = 20, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "has_marriage_contract", nullable = false)
    private Boolean marriageContract;

}
