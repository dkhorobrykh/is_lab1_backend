package ru.itmo.is.lab1.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.is.lab1.model.FuelType;
import ru.itmo.is.lab1.model.VehicleType;

import java.io.Serializable;

/**
 * DTO for {@link ru.itmo.is.lab1.model.Vehicle}
 */
@Data
@Builder
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class VehicleAddDto implements Serializable {
    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    String name;
    @NotNull(message = "Coordinates cannot be null")
    VehicleAddDto.CoordinatesDto coordinates;
//    @JsonProperty("vehicleType")
    VehicleType type;
    @Min(message = "EnginePower must be greater than 0", value = 1)
    Long enginePower;
    @Min(message = "NumberOfWheels must be greater than 0", value = 1)
    int numberOfWheels;
    @Positive(message = "Capacity must be greater than 0")
    float capacity;
    @Min(message = "DistanceTravelled must be greater than 0", value = 1)
    Double distanceTravelled;
    @Positive(message = "FuelConsumption must be greater than 0")
    double fuelConsumption;
    FuelType fuelType;
    boolean canBeEditedByAdmin;

    /**
     * DTO for {@link ru.itmo.is.lab1.model.Coordinates}
     */
    @Data
    @Builder
    @Jacksonized
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CoordinatesDto implements Serializable {
        Integer x;
        Float y;
    }
}