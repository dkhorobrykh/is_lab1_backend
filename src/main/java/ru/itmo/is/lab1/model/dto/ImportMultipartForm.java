package ru.itmo.is.lab1.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jboss.resteasy.annotations.jaxrs.FormParam;

import java.io.InputStream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportMultipartForm {
    @FormParam("file")
    private InputStream file;

    @FormParam("filename")
    private String filename;
}
