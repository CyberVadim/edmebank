package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.edmebank.contracts.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientProducts {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    public UUID clientId;
    public UUID productId;

    public LocalDate startDate;
    public LocalDate endDate;
    @Column(precision = 15, scale = 2)
    public BigDecimal currentBalance;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20)
    public ProductStatus status;

    @Column(columnDefinition = "jsonb")
    public String terms;
    public LocalDateTime lastUpdated;

}
