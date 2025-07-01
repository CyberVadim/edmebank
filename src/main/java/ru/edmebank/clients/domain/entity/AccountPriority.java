package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import ru.edmebank.contracts.enums.AccountPriorityStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "account_priorities", schema = "client_data")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccountPriority {
    private final Integer defaultVersion = 1;
    private final AccountPriorityStatus defaultStatus = AccountPriorityStatus.ACTIVE;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_account_priorities_accounts"))
    @NotNull
    private Account account;

    @Column(name = "account_type", length = 20, nullable = false)
    @Size(max = 20, message = "Тип счета не должен превышать 20 символов")
    @NotBlank
    private String accountType;

    @Column(name = "priority_write_off", nullable = false)
    @NotNull
    private Boolean priorityWriteOff;

    @Column(name = "priority_accrual", nullable = false)
    @NotNull
    private Boolean priorityAccrual;

    @Column(name = "can_set_write_off", nullable = false)
    @NotNull
    private Boolean canSetWriteOff = true;

    @Column(name = "can_set_accrual", nullable = false)
    @NotNull
    private Boolean canSetAccrual = true;

    @Column(name = "change_reasons", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> changeReasons;

    @Column(name = "change_reason", nullable = false, length = 500)
    @Size(max = 500, message = "Причина изменения не должна превышать 500 символов")
    @NotBlank
    private String changeReason;

    @Column(name = "initiator_id", nullable = false, length = 50)
    @Size(max = 50, message = "ID инициатора не должен превышать 50 символов")
    @NotBlank
    private String initiatorId;

    @Column(name = "initiator_name", nullable = false, length = 100)
    @Size(max = 100, message = "ФИО инициатора не должно превышать 100 символов")
    @NotBlank
    private String initiatorName;

    @Column(name = "initiator_role", nullable = false, length = 30)
    @Size(max = 30, message = "Роль инициатора не должна превышать 30 символов")
    @NotBlank
    private String initiatorRole;

    @Column(name = "last_updated_by", nullable = false, length = 100)
    @Size(max = 100, message = "Информация о пользователе не должна превышать 100 символов")
    @NotBlank
    private String lastUpdatedBy;

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

    @Column(name = "version", nullable = false)
    @NotNull
    @Version
    private Integer version = defaultVersion;

    @Column(name = "status", nullable = false, length = 20)
    @Size(max = 20, message = "Статус не должен превышать 20 символов")
    @NotBlank
    private AccountPriorityStatus status = defaultStatus;
}