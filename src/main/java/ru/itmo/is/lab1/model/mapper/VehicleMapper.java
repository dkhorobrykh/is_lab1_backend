package ru.itmo.is.lab1.model.mapper;

import org.mapstruct.*;
import ru.itmo.is.lab1.model.Vehicle;
import ru.itmo.is.lab1.model.dto.VehicleAddDto;
import ru.itmo.is.lab1.model.dto.VehicleDto;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.JAKARTA_CDI;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = JAKARTA_CDI)
public abstract class VehicleMapper {

    @Mapping(source = "user.id", target = "userId")
    public abstract VehicleDto toDto(Vehicle vehicle);

    public abstract List<VehicleDto> toDto(List<Vehicle> vehicles);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "coordinates.id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateObjectFromDto(VehicleAddDto dto, @MappingTarget Vehicle vehicle);
}