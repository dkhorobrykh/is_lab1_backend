package ru.itmo.is.lab1.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.is.lab1.model.FuelType;
import ru.itmo.is.lab1.model.VehicleType;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * DTO for {@link ru.itmo.is.lab1.model.Vehicle}
 */
@Data
@Builder
@Jacksonized
public class VehicleDto implements Serializable {
    @NotNull(message = "Id cannot be null")
    @Min(message = "Id must be greater than 0", value = 1)
    Long id;
    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    String name;
    CoordinateDto coordinates;
    @NotNull(message = "creationDate cannot be null")
    ZonedDateTime creationDate;
    VehicleType type;
    @Min(message = "EnginePower must be greater than 0", value = 1)
    Integer enginePower;
    @Min(message = "NumberOfWheels must be greater than 0", value = 1)
    long numberOfWheels;
    @Positive(message = "Capacity must be greater than 0")
    Double capacity;
    @Min(message = "DistanceTravelled must be greater than 0", value = 1)
    int distanceTravelled;
    @Positive(message = "FuelConsumption must be greater than 0")
    double fuelConsumption;
    FuelType fuelType;
    Long userId;
    private boolean canBeEditedByAdmin;

    @Data
    @Builder
    @Jacksonized
    public static class CoordinateDto implements Serializable {
        Integer id;
        Integer x;
        Float y;
    }
}