package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import jakarta.persistence.EmbeddedId;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import org.hibernate.annotations.CreationTimestamp;
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