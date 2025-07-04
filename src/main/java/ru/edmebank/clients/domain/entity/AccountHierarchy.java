package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_hierarchy", schema = "client_data")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccountHierarchy {
    @Id
    @Column(name = "child_account_id", nullable = false)
    @NotNull
    @EqualsAndHashCode.Include
    private UUID childAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id", nullable = true, foreignKey = @ForeignKey(name = "fk_account_hierarchy_parent"))
    private Account parentAccount;

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