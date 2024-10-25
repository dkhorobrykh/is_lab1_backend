package ru.itmo.is.lab1.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import ru.itmo.is.lab1.model.FuelType;
import ru.itmo.is.lab1.model.Vehicle;
import ru.itmo.is.lab1.model.VehicleType;

import java.util.ArrayList;
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

    public List<Vehicle> findWithFilters(String name, String fuelType, String vehicleType,
                                         String sortBy, boolean ascending, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vehicle> cq = cb.createQuery(Vehicle.class);
        Root<Vehicle> vehicleRoot = cq.from(Vehicle.class);

        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(cb.lower(vehicleRoot.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (fuelType != null && !fuelType.isEmpty()) {
            predicates.add(cb.equal(vehicleRoot.get("fuelType"), FuelType.valueOf(fuelType)));
        }

        if (vehicleType != null && !vehicleType.isBlank()) {
            predicates.add(cb.equal(vehicleRoot.get("type"), VehicleType.valueOf(vehicleType)));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        if (sortBy != null && !sortBy.isEmpty()) {
            if (ascending) {
                cq.orderBy(cb.asc(vehicleRoot.get(sortBy)));
            } else {
                cq.orderBy(cb.desc(vehicleRoot.get(sortBy)));
            }
        }

        TypedQuery<Vehicle> query = entityManager.createQuery(cq);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }
}
