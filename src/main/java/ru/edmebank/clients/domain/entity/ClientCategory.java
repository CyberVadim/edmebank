package ru.edmebank.clients.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.edmebank.contracts.enums.ClientCategoryEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "client_categories")
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ClientCategory {
    @Id
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ClientCategoryEnum category = ClientCategoryEnum.STANDARD;

    @Column(nullable = false)
    private Integer loyaltyPoints = 0;

    @CreationTimestamp
    @Column(updatable = false)
    public final LocalDateTime createdAt;

    @UpdateTimestamp
    public LocalDateTime updatedAt;
}
