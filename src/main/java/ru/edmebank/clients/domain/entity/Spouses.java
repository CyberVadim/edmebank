package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.edmebank.contracts.enums.SocialStatus;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
public class Spouses {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "client_id",
            referencedColumnName = "id",
            insertable = false
    )
    private Client client;

    @Column(unique = true , nullable = false)
    private UUID clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "spouse_client_id",
            referencedColumnName = "id",
            insertable = false
    )
    private Client spouseClientId;

    @Column(length = 255)
    private String fullName;

    @Column(nullable = false)
    private Date marriageDate;

    @Column()
    private Boolean marriageContractExists = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SocialStatus socialStatus;

    @Column()
    private Boolean isCurrent = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void validateSpouseInfo() {
        if (spouseClientId == null && fullName == null) {
            throw new IllegalStateException("Необходимо указать либо spouseClientId, либо fullName.");
        }
    }
}


