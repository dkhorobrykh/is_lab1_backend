package ru.itmo.is.lab1.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.model.dto.JwtResponse;
import ru.itmo.is.lab1.model.dto.UserAuthDto;
import ru.itmo.is.lab1.model.dto.UserDto;
import ru.itmo.is.lab1.service.UserService;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class AuthController {
    @Inject
    private UserService userService;

    @POST
    @Path("/register")
    public Response register(UserAuthDto userDto) {
        String token = userService.register(userDto.getUsername(), userDto.getPassword());
        return Response.ok(new JwtResponse(token)).build();
    }

    @POST
    @Path("/login")
    public Response login(UserAuthDto userDto) {
        String token = userService.login(userDto.getUsername(), userDto.getPassword());
        return Response.ok(new JwtResponse(token)).build();
    }
}
