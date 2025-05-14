package ru.edmebank.clients.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.edmebank.contracts.enums.RelationTypeEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "client_children")
public class ClientChildren {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type")
    private RelationTypeEnum relationType;

    @Column(name = "is_dependent")
    private Boolean isDependent = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
