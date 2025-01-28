package org.myteam.server.upload.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * 로컬 실행용 S3 Config
 */
@Configuration
public class S3ConfigLocal {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String minioAccessKey;

    @Value("${minio.secret-key}")
    private String minioSecretKey;

    @Value("${minio.region}")
    private String region;

    @Bean
    public S3Client minioClient() {
        return S3Client.builder()
                .endpointOverride(URI.create(minioUrl))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials
                                .create(minioAccessKey, minioSecretKey)))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .endpointOverride(URI.create(minioUrl))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials
                                .create(minioAccessKey, minioSecretKey)))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}