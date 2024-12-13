package ru.itmo.is.lab1.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.config.AWSCloudUtil;
import ru.itmo.is.lab1.exception.CustomException;
import ru.itmo.is.lab1.exception.ExceptionEnum;

import java.io.IOException;

@ApplicationScoped
@Slf4j
public class FileStoreService {

    @Inject
    private AWSCloudUtil awsCloudUtil;

    private final static String ACCESS_KEY = "XXX";
    private final static String SECRET_KEY = "XXX";
    private final static String BUCKET_NAME =  "is-lab3";

    public void uploadFile(String filename, byte[] fileBytes) {
        try {
            log.info("Uploading file: {}", filename);
            awsCloudUtil.uploadFileToS3(filename, fileBytes, ACCESS_KEY, SECRET_KEY, BUCKET_NAME);
            log.info("File uploaded successfully: {}", filename);
        } catch (CustomException e) {
            log.error("Failed to upload file: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("{}", (Object) e.getStackTrace());
            log.error("Unexpected error occurred while uploading file: {}", e.getMessage());
            throw new CustomException(ExceptionEnum.FILE_STORAGE_UNAVAILABLE);
        }
    }

    public byte[] downloadFile(String filename) {
        try {
            log.info("Downloading file: {}", filename);
            var s3InputStream = awsCloudUtil.downloadFileFromS3(filename, ACCESS_KEY, SECRET_KEY, BUCKET_NAME);
            return s3InputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Failed to read file bytes: {}", e.getMessage());
            throw new CustomException(ExceptionEnum.FILE_STORAGE_UNAVAILABLE);
        } catch (CustomException e) {
            log.error("Failed to download file: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while downloading file: {}", e.getMessage());
            throw new CustomException(ExceptionEnum.FILE_STORAGE_UNAVAILABLE);
        }
    }

    public void deleteFile(String filename) {
        try {
            log.info("Deleting file: {}", filename);
            awsCloudUtil.deleteFileFromS3(filename, ACCESS_KEY, SECRET_KEY, BUCKET_NAME);
            log.info("File deleted successfully: {}", filename);
        } catch (CustomException e) {
            log.error("Failed to delete file: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("{}", (Object) e.getStackTrace());
            log.error("Unexpected error occurred while deleting file: {}", e.getMessage());
            throw new CustomException(ExceptionEnum.FILE_STORAGE_UNAVAILABLE);
        }
    }
}
