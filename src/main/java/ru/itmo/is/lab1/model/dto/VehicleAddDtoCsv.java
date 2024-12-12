package ru.itmo.is.lab1.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.is.lab1.model.FuelType;
import ru.itmo.is.lab1.model.VehicleType;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
public class VehicleAddDtoCsv {
    String name;
    Integer coordinatesX;
    Float coordinatesY;
    VehicleType type;
    Long enginePower;
    int numberOfWheels;
    float capacity;
    Double distanceTravelled;
    double fuelConsumption;
    FuelType fuelType;
    boolean canBeEditedByAdmin;
}
