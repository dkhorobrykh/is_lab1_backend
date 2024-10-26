package ru.itmo.is.lab1.controller;

import jakarta.ejb.ObjectNotFoundException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.exception.CustomException;
import ru.itmo.is.lab1.exception.ExceptionEnum;
import ru.itmo.is.lab1.model.User;
import ru.itmo.is.lab1.model.Vehicle;
import ru.itmo.is.lab1.model.dto.VehicleAddDto;
import ru.itmo.is.lab1.model.mapper.VehicleMapper;
import ru.itmo.is.lab1.service.RoleService;
import ru.itmo.is.lab1.service.VehicleService;

@Path("/vehicle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class VehicleController {

    @Inject
    private VehicleService vehicleService;

    @Inject
    private VehicleMapper vehicleMapper;

    @Inject
    private RoleService roleService;

    @GET
    public Response getAllVehicles(@QueryParam("name") String name,
                                   @QueryParam("fuelType") String fuelType,
                                   @QueryParam("vehicleType") String vehicleType,
                                   @QueryParam("sortBy") @DefaultValue("id") String sortBy,
                                   @QueryParam("ascending") @DefaultValue("true") boolean ascending,
                                   @QueryParam("page") @DefaultValue("0") int page,
                                   @QueryParam("size") @DefaultValue("10") int size) {
        var result = vehicleService.getAllVehiclesWithFilters(name, fuelType, vehicleType, sortBy, ascending, page, size);

        return Response
                .status(Response.Status.OK)
                .entity(vehicleMapper.toDto(result))
                .build();
    }


    @POST
    @Path("/add")
    public Response addVehicle(VehicleAddDto dto, @Context SecurityContext securityContext) {
        var result = vehicleService.addVehicle(dto, securityContext);

        return Response
                .status(Response.Status.OK)
                .entity(vehicleMapper.toDto(result))
                .build();
    }

    @PUT
    @Path("/{vehicleId}")
    public Response updateVehicle(@PathParam("vehicleId") Long vehicleId, @Valid VehicleAddDto dto, @Context SecurityContext securityContext) throws ObjectNotFoundException {
        User currentUser = roleService.getCurrentUser(securityContext);
        Vehicle vehicle = vehicleService.getById(vehicleId);

        if (!vehicle.getUser().getId().equals(currentUser.getId())
                && !(vehicle.isCanBeEditedByAdmin() && currentUser.isAdmin()))
            throw new CustomException(ExceptionEnum.FORBIDDEN);

        var result = vehicleService.updateVehicle(vehicleId, dto);

        return Response
                .status(Response.Status.OK)
                .entity(vehicleMapper.toDto(result))
                .build();
    }

    @DELETE
    @Path("/{vehicleId}")
    public Response deleteVehicle(@PathParam("vehicleId") Long vehicleId, @Context SecurityContext securityContext) throws ObjectNotFoundException {
        User currentUser = roleService.getCurrentUser(securityContext);
        Vehicle vehicle = vehicleService.getById(vehicleId);

        if (!vehicle.getUser().getId().equals(currentUser.getId())
                && !(vehicle.isCanBeEditedByAdmin() && currentUser.isAdmin()))
            throw new CustomException(ExceptionEnum.FORBIDDEN);

        vehicleService.deleteVehicle(vehicleId);

        return Response
                .status(Response.Status.OK)
                .build();
    }
}
