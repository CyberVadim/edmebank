package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(
        name = "credit_history",
        indexes = @Index(name = "payment_date_id", columnList = "payment_date"))
public class CreditHistory {
    @Id
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private ClientProducts clientProduct;

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
