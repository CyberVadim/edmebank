package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "credit_history",
        indexes = @Index(name = "idx_payment_date", columnList = "payment_date"))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CreditHistory {

    @Id
    private UUID id;

    @Builder.Default
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_product_id", referencedColumnName = "id", nullable = false)
    private ClientProducts clientProduct = new ClientProducts();

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false, precision = 15, scale = 2)
    @Min(0)
    private BigDecimal scheduledAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    @Min(0)
    private BigDecimal paidAmount;

    @Column(nullable = false)
    @Min(0)
    private Integer delinquencyDays = 0;

    @Column(length = 30)
    private String paymentMethod;

    @CreationTimestamp
    @Column(updatable = false)
    public final LocalDateTime createdAt;

    @UpdateTimestamp
    public LocalDateTime updatedAt;
}
