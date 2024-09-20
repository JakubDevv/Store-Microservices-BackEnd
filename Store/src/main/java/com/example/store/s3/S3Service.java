package com.example.store.s3;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

@Service
public class S3Service {

    private final S3Client s3;

    public S3Service(S3Client s3) {
        this.s3 = s3;
    }

    public void putObject(String bucketName, String key, byte[] file){
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.putObject(objectRequest, RequestBody.fromBytes(file));
    }

    public byte[] getObject(String bucketName, String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3.getObject(getObjectRequest).readAllBytes();
    }

    public void deleteObject(String bucketName, String key){
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();

        s3.deleteObject(deleteObjectRequest);
    }

//    public void putObject(String bucketName, String key, byte[] file){ }
//
//    public byte[] getObject(String bucketName, String key) throws IOException {
//        return "asd".getBytes();
//    }
//
//    public void deleteObject(String bucketName, String key){ }
}
