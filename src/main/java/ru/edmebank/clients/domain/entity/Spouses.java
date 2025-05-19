package ru.edmebank.clients.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.edmebank.clients.utils.validation.AtLeastOneNotNull;
import ru.edmebank.contracts.enums.SocialStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@AtLeastOneNotNull(fields = {"spouseClientId", "fullName"})
public class Spouses {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "client_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
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

    @Column(length = 255)
    private String fullName;

    @Column(nullable = false)
    private LocalDate marriageDate;

    private Boolean marriageContractExists = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SocialStatus socialStatus;

    private Boolean isCurrent = true;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}