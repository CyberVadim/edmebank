package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "marriage_contracts")
@Getter
@Setter
@NoArgsConstructor
public class MarriageContract {

    @Id
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "spouse_id",
            referencedColumnName = "client_id",
            nullable = false)
    private Spouses spouse;

    @Column(length = 50, nullable = false)
    private String contractNumber;

    @Column(nullable = false)
    private LocalDate signingDate;

    @Column(columnDefinition = "text")
    private String notaryInfo;

    @Column(columnDefinition = "text", nullable = false)
    private String scanUrl;

    @Column(columnDefinition = "jsonb")
    private String terms;

    @Column(columnDefinition = "daterange")
    private String validityPeriod;

    @CreationTimestamp
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
