package ru.edmebank.clients.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import ru.edmebank.contracts.enums.ContactType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class Contacts {

    @EmbeddedId
    private ContactsId id;

    @MapsId("clientId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "client_id",
            foreignKey = @ForeignKey(name = "fk_contact_client"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ContactType", insertable = false, updatable = false)
    private ContactType type;

    @Column(length = 100, insertable = false, updatable = false)
    private String value;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Embeddable
    public record ContactsId(
            UUID clientId,

            @Enumerated(EnumType.STRING)
            @Column(columnDefinition = "ContactType", insertable = false, updatable = false)
            ContactType type,

            String value) implements Serializable {}
}
