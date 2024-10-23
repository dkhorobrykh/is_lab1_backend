package ru.itmo.is.lab1.model.mapper;

import org.mapstruct.*;
import ru.itmo.is.lab1.model.AdminRequest;
import ru.itmo.is.lab1.model.dto.AdminRequestDto;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.CDI, uses = {UserMapper.class})
public abstract class AdminRequestMapper {
    public abstract AdminRequest toEntity(AdminRequestDto adminRequestDto);

    public abstract AdminRequestDto toDto(AdminRequest adminRequest);
    public abstract List<AdminRequestDto> toDto(List<AdminRequest> adminRequestList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract AdminRequest partialUpdate(AdminRequestDto adminRequestDto, @MappingTarget AdminRequest adminRequest);
}