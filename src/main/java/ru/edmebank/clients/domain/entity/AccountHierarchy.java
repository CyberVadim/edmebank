package ru.edmebank.clients.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_hierarchy", schema = "client_data")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter
public class AccountHierarchy {

    @Id
    @Column(name = "child_account_id", nullable = false)
    @NotNull
    @EqualsAndHashCode.Include
    private UUID childAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_account_id", insertable = false, updatable = false, nullable = false,
            foreignKey = @ForeignKey(name = "fk_account_hierarchy_child"))
    @NotNull
    private Account childAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id", nullable = true,
            foreignKey = @ForeignKey(name = "fk_account_hierarchy_parent"))
    private Account parentAccount;

    @Column(name = "created_at", nullable = false, updatable = false)
    @NotNull
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @NotNull
    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;
}