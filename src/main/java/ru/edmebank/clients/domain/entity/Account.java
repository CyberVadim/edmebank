package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ! nullable = false в миграции защищает БД, а @Column(nullable = false) синхронизирует модель JPA с схемой.
 * @NotNull добавляет валидацию в коде, ловя null раньше. Дублирование нужно для надёжности и согласованности.
 * Поэтому еще и прописаны название таблиц в @Column, хоть может и само мапиться. Все для надеждности и согласованности.
 */

@Entity
@Table(name = "accounts", schema = "client_data")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {
    private final String defaultStatus = "открыт";
    private final BigDecimal defaultBalance = BigDecimal.ZERO;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id", nullable = false)
    @EqualsAndHashCode.Include
    private UUID accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_accounts_clients"))
    @NotNull
    private Client client;

    @Column(name = "account_type", length = 50)
    @Size(max = 50, message = "Тип счета не должен превышать 50 символов")
    private String accountType;

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    @NotNull
    private BigDecimal balance = defaultBalance;

    @Column(name = "currency", nullable = false, length = 3)
    @Size(min = 3, max = 3, message = "Код валюты должен быть строго 3 символа")
    @NotBlank
    private String currency;

    @Column(name = "status", nullable = false, length = 20)
    @Size(max = 20, message = "Статус счета не должен превышать 20 символов")
    @NotBlank
    private String status = defaultStatus;

    @Column(name = "opened_at", nullable = false)
    @NotNull
    @CreationTimestamp
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @NotNull
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @NotNull
    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;
}