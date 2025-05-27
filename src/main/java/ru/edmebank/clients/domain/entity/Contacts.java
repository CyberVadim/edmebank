package ru.edmebank.clients.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import ru.edmebank.contracts.enums.ContactType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
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
