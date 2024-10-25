package ru.itmo.is.lab1.controller;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.exception.CustomException;
import ru.itmo.is.lab1.exception.ExceptionEnum;
import ru.itmo.is.lab1.model.AdminRequest;
import ru.itmo.is.lab1.model.JwtAuthentication;
import ru.itmo.is.lab1.model.User;
import ru.itmo.is.lab1.model.dto.UserCredentialDto;
import ru.itmo.is.lab1.model.mapper.AdminRequestMapper;
import ru.itmo.is.lab1.model.mapper.UserMapper;
import ru.itmo.is.lab1.repository.AdminRequestRepository;
import ru.itmo.is.lab1.service.JwtProvider;
import ru.itmo.is.lab1.service.RoleService;
import ru.itmo.is.lab1.service.UserService;
import ru.itmo.is.lab1.util.RoleAllowed;

import java.security.Principal;
import java.util.List;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
@DeclareRoles({"ADMIN", "USER"})
public class UserController {

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private AdminRequestMapper adminRequestMapper;

    @Inject
    private UserMapper userMapper;

    @GET
    @Path("/info")
    public Response getUser(@Context SecurityContext securityContext) {
        User currentUser = roleService.getCurrentUser(securityContext);

        return Response
                .status(Response.Status.OK)
                .entity(userMapper.toDto(currentUser))
                .build();
    }

    @POST
    @Path("/admin/request")
    public Response makeAdminRequest(@Context SecurityContext securityContext) {
        userService.makeAdminRequest(securityContext);

        return Response.ok().build();
    }

    @GET
    @Path("/admin/requests")
    @RoleAllowed("ADMIN")
    public Response getAdminRequests() {
        List<AdminRequest> result = userService.getAllActiveAdminRequests();

        return Response.ok(adminRequestMapper.toDto(result)).build();
    }

    @POST
    @Path("/admin/request/{requestId}/approve")
    @RoleAllowed("ADMIN")
    public Response approveAdminRequest(@PathParam("requestId") Long requestId, @Context SecurityContext securityContext) {
        userService.approveAdminRequest(requestId, securityContext);

        return Response.ok().build();
    }

    @POST
    @Path("/admin/request/{requestId}/decline")
    @RoleAllowed("ADMIN")
    public Response declineAdminRequest(@PathParam("requestId") Long requestId, @Context SecurityContext securityContext) {
        userService.declineAdminRequest(requestId, securityContext);

        return Response.ok().build();
    }
}
