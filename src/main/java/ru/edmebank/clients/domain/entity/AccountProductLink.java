package ru.edmebank.clients.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_product_links", schema = "client_data")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccountProductLink {

    @EmbeddedId
    @NotNull
    @EqualsAndHashCode.Include
    private AccountProductLinkId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false, nullable = false,
            foreignKey = @ForeignKey(name = "fk_account_product_links_accounts"))
    @NotNull
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_product_id", insertable = false, updatable = false, nullable = false,
            foreignKey = @ForeignKey(name = "fk_account_product_links_client_products"))
    @NotNull
    private ClientProducts clientProduct;

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