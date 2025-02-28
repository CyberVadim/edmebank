package com.edmebank.clientmanagement.model.bank_product;

import com.edmebank.clientmanagement.model.Client;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue
    private UUID id;
    private String accountNumber;
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}

