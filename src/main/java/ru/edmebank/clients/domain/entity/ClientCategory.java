package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(name = "client_categories")
public class ClientCategory {
    @Id
    private UUID id;

    @MapsId
    @OneToOne
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientCategoryEnum category = ClientCategoryEnum.STANDARD;

    @Column(nullable = false)
    private Integer loyaltyPoints = 0;

    @CreationTimestamp
    @Column(updatable = false)
    public final LocalDateTime createAt;

    @UpdateTimestamp
    public LocalDateTime updateAt;
}
