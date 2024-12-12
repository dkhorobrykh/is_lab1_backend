package ru.itmo.is.lab1.controller;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import ru.itmo.is.lab1.model.dto.ImportMultipartForm;
import ru.itmo.is.lab1.service.ImportService;

import java.io.InputStream;

@Path("import")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Slf4j
public class ImportController {
    @Inject
    private ImportService importService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response importObjects(
            @MultipartForm ImportMultipartForm multipartForm,
            @Context SecurityContext securityContext
            ) {
        String filename = multipartForm.getFilename();
        InputStream uploadedInputStream = multipartForm.getFile();
        importService.importObjects(uploadedInputStream, filename, securityContext);
        return Response.ok().build();
    }

    @GET
    public Response getImportHistory(@Context SecurityContext securityContext) {
        var result = importService.getImportHistory(securityContext);

        return Response.ok(result).build();
    }
}
