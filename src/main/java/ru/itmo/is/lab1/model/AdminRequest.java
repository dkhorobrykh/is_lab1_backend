package ru.itmo.is.lab1.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table(
        name = "IS_admin_request",
        schema = "s367595"
)
@Builder
@AllArgsConstructor
@Jacksonized
@EntityListeners(AuditListener.class)
public class AdminRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_datetime", nullable = false)
    @Builder.Default
    private Instant createdDatetime = Instant.now();

    @Column(name = "is_approved", nullable = false)
    @Builder.Default
    private boolean approved = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;
}
