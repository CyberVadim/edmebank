package ru.edmebank.clients.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.edmebank.contracts.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientProducts {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    public UUID clientId;
    public UUID productId;

    public Date startDate;
    public Date endDate;
    @Column(precision = 15, scale = 2)
    public BigDecimal currentBalance;

    @Enumerated(value = EnumType.STRING)
    public ProductStatus status;

    @Column(columnDefinition = "jsonb")
    public String terms;
    public LocalDateTime lastUpdated;

}
