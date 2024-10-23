package ru.itmo.is.lab1.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.service.VehicleService;

import java.util.Map;

@Path("/vehicle/query")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class VehicleQueriesController {

    @Inject
    private VehicleService vehicleService;

    @GET
    @Path("group-by-engine-power")
    public Response groupByEnginePower() {
        var result = vehicleService.groupByEnginePower();

        return Response.ok(result).build();
    }

    @GET
    @Path("count-by-fuel-consumption/{neededFuelConsumption}")
    public Response countByFuelConsumption(@PathParam(value = "neededFuelConsumption") Double neededFuelConsumption) {
        var result = vehicleService.countByFuelConsumption(neededFuelConsumption);

        return Response.ok(result).build();
    }

    @GET
    @Path("count-by-fuel-type-less-than/{neededFuelType}")
    public Response countByFuelTypeLessThan(@PathParam(value = "neededFuelType") String neededFuelType) {
        var result = vehicleService.countByFuelTypeLessThan(neededFuelType);

        return Response.ok(result).build();
    }

    @GET
    @Path("find-by-engine-power-range/{minPower}/{maxPower}")
    public Response findByEnginePowerRange(@PathParam(value = "minPower") Integer minPower, @PathParam(value = "maxPower") Integer maxPower) {
        var result = vehicleService.findByEnginePowerRange(minPower, maxPower);

        System.out.println(result);

        return Response.ok(result).build();
    }

    @GET
    @Path("find-by-wheel-count-range/{minNumber}/{maxNumber}")
    public Response findByWheelCountRange(@PathParam(value = "minNumber") Integer minNumber, @PathParam(value = "maxNumber") Integer maxNumber) {
        var result = vehicleService.findByWheelCountRange(minNumber, maxNumber);

        return Response.ok(result).build();
    }
}
