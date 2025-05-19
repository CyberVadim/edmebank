package ru.edmebank.clients.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.edmebank.contracts.enums.RelationTypeEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "client_children")
public class ClientChildren {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    private Client client;

    private String fullName;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private RelationTypeEnum relationType;

    private Boolean isDependent = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

}
