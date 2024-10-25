package ru.itmo.is.lab1.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "is_audit_log",
        schema = "s367595"
)
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "operation")
    private String operation;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "timestamp")
    private Instant timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

}
