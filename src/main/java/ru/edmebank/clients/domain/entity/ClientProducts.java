package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_client_products_client_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    public Client client;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_client_products_product_id"))
    public FinancialProducts product;

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
