package ru.itmo.is.lab1.repository;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import ru.itmo.is.lab1.model.Vehicle;

import java.util.List;

@ApplicationScoped
public class VehicleRepository extends AbstractRepository<Vehicle, Long> {

    public VehicleRepository() {
        super(Vehicle.class);
    }

    public List<Vehicle> getAll() {
        return entityManager
                .createQuery("""
                        SELECT vehicle
                        FROM Vehicle vehicle
                        ORDER BY vehicle.id
                        """, Vehicle.class)
                .getResultList();
    }

    public List<?> groupByEnginePower() {
        return entityManager
                .createNativeQuery("SELECT * FROM group_by_engine_power()")
                .getResultList();
    }

    public Integer countByFuelConsumption(Double fuelConsumption) {
        return (Integer) entityManager
                .createNativeQuery("SELECT * FROM count_by_fuel_consumption(:fuelConsumption)")
                .setParameter("fuelConsumption", fuelConsumption)
                .getSingleResult();
    }

    public Integer countByFuelTypeLessThan(String fuelType) {
        return (Integer) entityManager
                .createNativeQuery("SELECT * FROM count_by_fuel_type_less_than(:fuelType)")
                .setParameter("fuelType", fuelType)
                .getSingleResult();
    }

    public List<Vehicle> findByEnginePowerRange(Integer minPower, Integer maxPower) {
        return entityManager
                .createNativeQuery("SELECT * FROM find_by_engine_power_range(:minPower, :maxPower)", Vehicle.class)
                .setParameter("minPower", minPower)
                .setParameter("maxPower", maxPower)
                .getResultList();
    }

    public List<Vehicle> findByWheelCountRange(Integer minNumber, Integer maxNumber) {
        return entityManager
                .createNativeQuery("SELECT * FROM find_by_wheel_count_range(:minNumber, :maxNumber)", Vehicle.class)
                .setParameter("minNumber", minNumber)
                .setParameter("maxNumber", maxNumber)
                .getResultList();
    }
}
