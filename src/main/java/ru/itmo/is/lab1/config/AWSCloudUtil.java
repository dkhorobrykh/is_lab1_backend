package ru.itmo.is.lab1.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.is.lab1.exception.CustomException;
import ru.itmo.is.lab1.exception.ExceptionEnum;

import java.io.*;

@ApplicationScoped
@Slf4j
public class AWSCloudUtil {

    private AWSCredentials awsCredentials(String accessKey, String secretKey) {
        return new BasicAWSCredentials(
                accessKey,
                secretKey
        );
    }

    private AmazonS3 awsS3ClientBuilder(String accessKey, String secretKey) {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net", "ru-central1"
                        )
                )
                .build();
    }

    public void uploadFileToS3(String filename, byte[] filebytes, String accessKey, String secretKey, String bucket) {
        AmazonS3 s3Client = awsS3ClientBuilder(accessKey, secretKey);

        File file = new File(filename);

        try (OutputStream os = new FileOutputStream(file)) {
            os.write(filebytes);
        } catch (FileNotFoundException ex) {
            log.error(ex.getMessage());
            throw new CustomException(ExceptionEnum.FILE_STORAGE_UNAVAILABLE);
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new CustomException(ExceptionEnum.FILE_STORAGE_UNAVAILABLE);
        }

        try {
            s3Client.putObject(bucket, filename, file);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new CustomException(ExceptionEnum.FILE_STORAGE_UNAVAILABLE);
        }
    }

    public S3ObjectInputStream downloadFileFromS3(String filename, String accessKey, String secretKey, String bucket) {
        AmazonS3 s3client = awsS3ClientBuilder(accessKey, secretKey);
        S3Object s3Object = s3client.getObject(bucket, filename);
        return s3Object.getObjectContent();
    }

    public void deleteFileFromS3(String filename, String accessKey, String secretKey, String bucket) {
        AmazonS3 s3client = awsS3ClientBuilder(accessKey, secretKey);
        try {
            s3client.deleteObject(bucket, filename);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new CustomException(ExceptionEnum.SERVER_ERROR);
        }
    }
}
