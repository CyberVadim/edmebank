package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.edmebank.contracts.enums.ClientCategoryEnum;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Назначение: Категории клиентов
 * Определяет персонализированные категории для клиентов (VIP, студент и т.д.).
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "client_categories")
public class ClientCategory {
    @Id
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ClientCategoryEnum category;

    @Builder.Default
    @Column(name = "loyalty_points", nullable = false)
    private Integer loyaltyPoints = 0;

    @Column(name = "last_category_update")
    private LocalDate lastCategoryUpdate;
}
