package ru.itmo.is.lab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.ZonedDateTime;

@Builder
@Entity
@Table(
        name = "IS_vehicle",
        schema = "s367595"
//        uniqueConstraints = {@UniqueConstraint(columnNames = "id"), @UniqueConstraint(columnNames = {"number_of_wheels", "capacity"})}
)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditListener.class)
@Jacksonized
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Min(value = 1, message = "Id must be greater than 0")
    private Long id;

    @NotEmpty(message = "Name cannot be empty")
    @NotNull(message = "Name cannot be null")
    @Column(name = "name")
    private String name;

    @ToString.Exclude
    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
    @JoinColumn(name = "coordinates_id", nullable = false)
    @NotNull(message = "Coordinates cannot be null")
    private Coordinates coordinates;

    @NotNull(message = "creationDate cannot be null")
    @Builder.Default
    @Column(name = "creation_date")
    private ZonedDateTime creationDate = ZonedDateTime.now();

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private VehicleType type;

    @Min(value = 1, message = "EnginePower must be greater than 0")
    @Column(name = "engine_power")
    private Long enginePower;

    @Min(value = 1, message = "NumberOfWheels must be greater than 0")
    @NotNull(message = "numberOfWheels cannot be null")
    @Column(name = "number_of_wheels")
    private int numberOfWheels;

    @Positive(message = "Capacity must be greater than 0")
    @Column(name = "capacity")
    private float capacity;

    @Min(value = 1, message = "DistanceTravelled must be greater than 0")
    @NotNull(message = "DistanceTravelled cannot be null")
    @Column(name = "distance_travelled")
    private Double distanceTravelled;

    @Positive(message = "FuelConsumption must be greater than 0")
    @NotNull(message = "FuelConsumption cannot be null")
    @Column(name = "fuel_consumption")
    private double fuelConsumption;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "owned_by", nullable = false)
    private User user;

    @Column(name = "can_be_edited_by_admin")
    @Builder.Default
    private boolean canBeEditedByAdmin = true;

    @Version
    private Integer version;
}
