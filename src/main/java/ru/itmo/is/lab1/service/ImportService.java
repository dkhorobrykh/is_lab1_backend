package ru.itmo.is.lab1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.*;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.exception.CustomException;
import ru.itmo.is.lab1.exception.ExceptionEnum;
import ru.itmo.is.lab1.model.ImportHistory;
import ru.itmo.is.lab1.model.Vehicle;
import ru.itmo.is.lab1.model.dto.VehicleAddDto;
import ru.itmo.is.lab1.model.dto.VehicleAddDtoCsv;
import ru.itmo.is.lab1.repository.ImportRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
@Slf4j
public class ImportService {
    @Inject
    private ImportRepository importRepository;

    @Inject
    private VehicleService vehicleService;

    @Inject
    private RoleService roleService;

    @Resource
    private UserTransaction userTransaction;

    private final static Object lock = new Object();

    public void importObjects(InputStream inputStream, String filename, SecurityContext securityContext) {
        List<VehicleAddDto> entities;

        String content;
        try {
            content = readInputStreamAsString(inputStream);
        } catch (IOException ex) {
            return;
        }
        InputStream parsedInput = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        try {
            if (filename == null || filename.endsWith(".json")) {
                ObjectMapper objectMapper = new ObjectMapper();
                entities = objectMapper.readValue(parsedInput, objectMapper.getTypeFactory().constructCollectionType(List.class, VehicleAddDto.class));
            } else if (filename.endsWith(".csv")) {
                List<VehicleAddDtoCsv> csvEntities = new CsvToBeanBuilder<VehicleAddDtoCsv>(new InputStreamReader(parsedInput))
                        .withType(VehicleAddDtoCsv.class)
                        .build()
                        .parse();

                entities = csvEntities.stream()
                        .map(ImportService::fromCsv)
                        .toList();
            } else {
                throw new CustomException(ExceptionEnum.BAD_FILE_FORMAT);
            }

            try {
                userTransaction.begin();
                processEntities(entities, securityContext);
                saveImportHistory(content, entities.size(), securityContext);
                userTransaction.commit();
            } catch (ConstraintViolationException violationException) {
                saveWithException(content, securityContext);

                try {
                    userTransaction.rollback();
                } catch (SystemException ex) {
                    log.error("Error with transaction rollback");
                    throw new CustomException(ExceptionEnum.SERVER_ERROR);
                }

                throw new CustomException(ExceptionEnum.BAD_FILE_CONTENT);
            }

        } catch (CustomException ex) {

            saveWithException(content, securityContext);

            throw ex;

        } catch (Exception e) {

            log.error(e.getMessage());

            saveWithException(content, securityContext);

            throw new CustomException(ExceptionEnum.BAD_FILE_CONTENT);
        }
    }

    @Transactional
    private void processEntities(@Valid List<VehicleAddDto> entities, SecurityContext securityContext) {
        log.info("Import processing start...");
        var res = new LinkedList<Vehicle>();
        AtomicInteger i = new AtomicInteger(1);
        entities.forEach(v -> {
            res.add(vehicleService.buildVehicle(v, securityContext));
            log.info("Entity {} successfully processed", i);
            i.getAndIncrement();
        });
        synchronized (lock) {
            vehicleService.checkEnginePowerAndNumberOfWheelsUniqueOrThrow(res);
            vehicleService.saveAll(res);
        }
        log.info("Import processing end...");
    }

    public static VehicleAddDto fromCsv(VehicleAddDtoCsv csv) {
        return VehicleAddDto.builder()
                .name(csv.getName())
                .coordinates(new VehicleAddDto.CoordinatesDto(csv.getCoordinatesX(), csv.getCoordinatesY()))
                .type(csv.getType())
                .enginePower(csv.getEnginePower())
                .numberOfWheels(csv.getNumberOfWheels())
                .capacity(csv.getCapacity())
                .distanceTravelled(csv.getDistanceTravelled())
                .fuelConsumption(csv.getFuelConsumption())
                .fuelType(csv.getFuelType())
                .canBeEditedByAdmin(csv.isCanBeEditedByAdmin())
                .build();
    }

    public String readInputStreamAsString(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    public List<ImportHistory> getImportHistory(SecurityContext securityContext) {
        if (roleService.getCurrentUser(securityContext).isAdmin()) {
            return importRepository.findAll();
        } else {
            return importRepository.findAllByUser_Id(roleService.getCurrentUser(securityContext).getId());
        }
    }

    @Transactional
    public void saveImportHistory(String content, Integer successObjects, SecurityContext securityContext) {
        importRepository.save(ImportHistory.builder()
                .user(roleService.getCurrentUser(securityContext))
                .fileInfo(content)
                .success(successObjects > 0)
                .successObjects(successObjects)
                .build());
    }

    private void saveWithException(String content, SecurityContext securityContext) {
        try {
            userTransaction.begin();
            saveImportHistory(content, 0, securityContext);
            userTransaction.commit();
        } catch (CustomException ce) {
            try {
                userTransaction.rollback();
            } catch (Exception exception) {
                log.error("Rollback exception {}", exception.getMessage());
            }
            throw ce;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicRollbackException |
                 HeuristicMixedException ex) {
            try {
                userTransaction.rollback();
            } catch (SystemException exception) {
                log.error("Rollback exception {}", exception.getMessage());
            }
            log.info("Transaction was rolled back: {}", ex.getMessage());
        }
    }

}
