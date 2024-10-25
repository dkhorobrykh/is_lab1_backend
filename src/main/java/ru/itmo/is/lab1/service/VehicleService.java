package ru.itmo.is.lab1.service;

import jakarta.ejb.ObjectNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.exception.CustomException;
import ru.itmo.is.lab1.exception.ExceptionEnum;
import ru.itmo.is.lab1.model.Coordinates;
import ru.itmo.is.lab1.model.Vehicle;
import ru.itmo.is.lab1.model.dto.VehicleAddDto;
import ru.itmo.is.lab1.model.mapper.VehicleMapper;
import ru.itmo.is.lab1.repository.VehicleRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Slf4j
public class VehicleService {
    @Inject
    private VehicleRepository vehicleRepository;

    @Inject
    private VehicleMapper vehicleMapper;

    @Inject
    private RoleService roleService;

    public List<Vehicle> getAll() {
        return vehicleRepository.getAll();
    }

    public List<Vehicle> getAllVehiclesWithFilters(String name, String fuelType, String vehicleType,
                                                      String sortBy, boolean ascending, int page, int size) {
        return vehicleRepository.findWithFilters(name, fuelType, vehicleType, sortBy, ascending, page, size);
    }

    public Vehicle getById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionEnum.VEHICLE_NOT_FOUND));
    }

    public Vehicle addVehicle(VehicleAddDto dto, SecurityContext securityContext) {
        Vehicle newVehicle = Vehicle.builder()

                .name(dto.getName())
                .coordinates(
                        Coordinates.builder()

                                .x(dto.getCoordinates().getX())
                                .y(dto.getCoordinates().getY())

                                .build()
                )
                .type(dto.getType())
                .enginePower(dto.getEnginePower())
                .numberOfWheels(dto.getNumberOfWheels())
                .capacity(dto.getCapacity())
                .distanceTravelled(dto.getDistanceTravelled())
                .fuelConsumption(dto.getFuelConsumption())
                .fuelType(dto.getFuelType())
                .user(roleService.getCurrentUser(securityContext))
                .canBeEditedByAdmin(dto.isCanBeEditedByAdmin())

                .build();

        return vehicleRepository.save(newVehicle);
    }

    public Vehicle updateVehicle(Long vehicleId, VehicleAddDto dto) throws ObjectNotFoundException {
        Vehicle vehicle = getById(vehicleId);

        vehicleMapper.updateObjectFromDto(dto, vehicle);

        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long vehicleId) throws ObjectNotFoundException {
        Vehicle vehicle = getById(vehicleId);

        vehicleRepository.delete(vehicle);
    }

    public List<List<Long>> groupByEnginePower() {
        return (List<List<Long>>) vehicleRepository.groupByEnginePower();
    }

    public Integer countByFuelConsumption(Double neededFuelConsumption) {
        return vehicleRepository.countByFuelConsumption(neededFuelConsumption);
    }

    public Integer countByFuelTypeLessThan(String fuelType) {
        return vehicleRepository.countByFuelTypeLessThan(fuelType);
    }

    public List<Vehicle> findByEnginePowerRange(Integer minPower, Integer maxPower) {
        return vehicleRepository.findByEnginePowerRange(minPower, maxPower);
    }

    public List<Vehicle> findByWheelCountRange(Integer minNumber, Integer maxNumber) {
        return vehicleRepository.findByWheelCountRange(minNumber, maxNumber);
    }
}
