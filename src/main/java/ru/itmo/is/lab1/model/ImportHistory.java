package ru.itmo.is.lab1.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "is_import_history",
        schema = "s367595"
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

//    @Lob
    @Column(name = "file_info", nullable = false, columnDefinition = "TEXT")
    private String fileInfo;

    @Builder.Default
    @Column(name = "creation_datetime", nullable = false)
    private Instant creationDatetime = Instant.now();

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Column(name = "success_objects", nullable = false)
    private Integer successObjects = 0;

    @Builder.Default
    @Column(name = "is_success", nullable = false)
    private boolean success = false;

    @Column(name = "filename")
    private String filename;

}
