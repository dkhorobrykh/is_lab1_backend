package ru.itmo.is.lab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Entity
@Table(
        name = "IS_Coordinates",
        schema = "s367595"
)
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Jacksonized
@EntityListeners(AuditListener.class)
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "x")
    @NotNull(message = "X cannot be null")
    private Integer x;

    @Column(name = "y")
    @NotNull(message = "Y cannot be null")
    private Float y;
}
