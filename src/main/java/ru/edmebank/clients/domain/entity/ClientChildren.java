package ru.edmebank.clients.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.edmebank.contracts.enums.RelationTypeEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "client_children")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientChildren {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    private Client client;

    private String fullName;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private RelationTypeEnum relationType;

    private Boolean isDependent = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

}
