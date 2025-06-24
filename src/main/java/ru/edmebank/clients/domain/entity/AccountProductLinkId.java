package ru.edmebank.clients.domain.entity;


import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class AccountProductLinkId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_account_product_links_accounts"))
    @NotNull
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_account_product_links_client_products"))
    @NotNull
    private ClientProducts clientProduct;

    public AccountProductLinkId(Account account, ClientProducts clientProduct) {
        this.account = account;
        this.clientProduct = clientProduct;
    }
}