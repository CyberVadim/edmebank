package ru.edmebank.clients.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.edmebank.clients.utils.validation.AtLeastOneNotNull;
import ru.edmebank.contracts.enums.EmploymentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@AtLeastOneNotNull(fields = {"spouseClientId", "fullName"})
public class Spouses {

    @Id
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "client_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Client client;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "spouse_client_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private Client spouseClientId;

    private String fullName;

    @Column(nullable = false)
    private LocalDate marriageDate;

    private Boolean marriageContractExists = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private EmploymentType socialStatus;

    private Boolean isCurrent = true;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}