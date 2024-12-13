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
import org.apache.commons.io.input.TeeInputStream;
import ru.itmo.is.lab1.exception.CustomException;
import ru.itmo.is.lab1.exception.ExceptionEnum;
import ru.itmo.is.lab1.model.ImportHistory;
import ru.itmo.is.lab1.model.Vehicle;
import ru.itmo.is.lab1.model.dto.VehicleAddDto;
import ru.itmo.is.lab1.model.dto.VehicleAddDtoCsv;
import ru.itmo.is.lab1.repository.ImportRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
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
    private final static Object lock2 = new Object();

    @Inject
    private FileStoreService fileStoreService;

    public void importObjects(InputStream inputStream, String filename, SecurityContext securityContext) {
        List<VehicleAddDto> entities;

        String content;
        byte[] fileBytes;

        filename = filename == null ? "lab2_json_50.json" : roleService.getCurrentUser(securityContext).getId() + "__" + Instant.now().toEpochMilli() + "__" + filename;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             TeeInputStream teeInputStream = new TeeInputStream(inputStream, byteArrayOutputStream)) {

            content = readInputStreamAsString(teeInputStream);

            fileBytes = byteArrayOutputStream.toByteArray();

        } catch (IOException ex) {
            log.error("Error reading input stream: {}", ex.getMessage());
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
                userTransaction.begin(); // первая фаза
                processEntities(entities, securityContext); // подготовка сущностей бд
                fileStoreService.uploadFile(filename, fileBytes); // загрузка файла в хранилище
//                if (1 == 1)
//                    throw new RuntimeException("TESTING");
                saveImportHistory(content, entities.size(), securityContext, filename);
                userTransaction.commit(); // если ошибок не возникло - все готовы, подтверждаем транзакцию во второй фазе
            } catch (ConstraintViolationException |
                     CustomException violationException) { // ошибочный файл, сохраняем ошибку

                if (violationException instanceof CustomException)
                    if (!((CustomException) violationException).getExceptionEnum().name().equals(ExceptionEnum.VALIDATION_EXCEPTION.name()))
                        throw violationException;
//
//                filename = "error_file_" + filename;
//                fileStoreService.uploadFile(filename, fileBytes);
//                saveWithException(content, securityContext, filename);

                try {
                    userTransaction.rollback();
                } catch (SystemException ex) {
                    log.error("Error with transaction rollback");
                    throw new CustomException(ExceptionEnum.SERVER_ERROR);
                } catch (IllegalStateException ignored) {}

                if (!violationException.getMessage().contains("TESTING"))
                    throw new CustomException(ExceptionEnum.BAD_FILE_CONTENT);
                else
                    throw new CustomException(ExceptionEnum.SERVER_ERROR);
            }

        } catch (CustomException ex) {

            log.error("Error processing file: {}", ex.getMessage());

            filename = "error_file_" + filename;

            try {
                userTransaction.rollback();
            } catch (SystemException e) {
                log.error("{}", e.getMessage());
//                throw new CustomException(ExceptionEnum.SERVER_ERROR);
            } catch (IllegalStateException ignored) {}

            if (!ex.getExceptionEnum().name().equals(ExceptionEnum.FILE_STORAGE_UNAVAILABLE.name()))
                fileStoreService.uploadFile(filename, fileBytes);
            else
                filename = null;

            if (!ex.getExceptionEnum().name().equals(ExceptionEnum.DATA_BASE_UNAVAILABLE.name())) {
                saveWithException(content, securityContext, filename);
            }

            throw ex;

        } catch (Exception e) {

            filename = "error_file_" + filename;

            log.error(e.getMessage());

            try {
                userTransaction.rollback();
            } catch (SystemException exception) {
                log.error("{}", exception.getMessage());
//                throw new CustomException(ExceptionEnum.SERVER_ERROR);
            } catch (IllegalStateException ignored) {}

            fileStoreService.uploadFile(filename, fileBytes);
            saveWithException(content, securityContext, filename);

            if (!e.getMessage().equals("TESTING"))
                throw new CustomException(ExceptionEnum.BAD_FILE_CONTENT);
            else
                throw new CustomException(ExceptionEnum.SERVER_ERROR);
        } catch (Error error) {
            try {
                userTransaction.rollback();
            } catch (SystemException ex) {
                log.error("{}", ex.getMessage());
//                throw new CustomException(ExceptionEnum.SERVER_ERROR);
            } catch (IllegalStateException ignored) {}
            throw new CustomException(ExceptionEnum.SERVER_ERROR);
        }
    }

    @Transactional
    private void processEntities(@Valid List<VehicleAddDto> entities, SecurityContext securityContext) {
        synchronized (lock) {
            log.info("Import processing start...");
            var res = new LinkedList<Vehicle>();
            AtomicInteger i = new AtomicInteger(1);
            entities.forEach(v -> {
                res.add(vehicleService.buildVehicle(v, securityContext));
                log.info("Entity {} successfully processed", i);
                i.getAndIncrement();
            });
            try {
                vehicleService.checkEnginePowerAndNumberOfWheelsUniqueOrThrow(res);
                vehicleService.saveAll(res);
            } catch (CustomException ex) {
                try {
                    userTransaction.rollback();
                    throw ex;
                } catch (SystemException exception) {
                    log.error("Rollback exception {}", exception.getMessage());
                }
            }

            log.info("Import processing end...");
        }
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

    public void saveImportHistory(String content, Integer successObjects, SecurityContext securityContext, String filename) {
        try {
            importRepository.save(ImportHistory.builder()
                    .user(roleService.getCurrentUser(securityContext))
                    .fileInfo(content)
                    .success(successObjects > 0)
                    .successObjects(successObjects)
                    .filename(filename)
                    .build());
        } catch (Exception ex) {
            throw new CustomException(ExceptionEnum.DATA_BASE_UNAVAILABLE);
        }

    }

    private void saveWithException(String content, SecurityContext securityContext, String filename) {
        try {
            userTransaction.begin();
            saveImportHistory(content, 0, securityContext, filename);
            userTransaction.commit();
        } catch (CustomException ce) {
            log.error("Error saving import history: {}", ce.getMessage());
            try {
                userTransaction.rollback();
            } catch (Exception exception) {
                log.error("Rollback exception {}", exception.getMessage());
            }
            throw ce;
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicRollbackException |
                 HeuristicMixedException ex) {
            log.error("Error saving import history: {}", ex.getMessage());
            try {
                userTransaction.rollback();
            } catch (SystemException exception) {
                log.error("Rollback exception {}", exception.getMessage());
            }
            log.info("Transaction was rolled back: {}", ex.getMessage());
        }
    }

}
