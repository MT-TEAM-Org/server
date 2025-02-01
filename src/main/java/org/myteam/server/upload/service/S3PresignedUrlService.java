package org.myteam.server.upload.service;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.upload.controller.response.S3FileUploadResponse;
import org.myteam.server.upload.domain.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3PresignedUrlService {

    @Value("${minio.bucket}")
    private String bucket;
    private final S3Presigner s3Presigner;

    /**
     * Presigned URL 생성
     *
     * @param fileName    업로드할 파일의 이름
     * @param contentType 파일의 MIME 타입
     */
    public S3FileUploadResponse generatePresignedUrl(String fileName, String contentType) {

        // 미디어 타입 & 파일명 검사
        verifyMimeType(contentType, fileName);

        // 폴더 (image or video) 설정
        String feature = contentType.split("/")[0].toLowerCase().equals("image") ? "image" : "video";

        // 파일명은 고유하도록 UUID 설정
        String uniqueFileName = feature + "/" + UUID.randomUUID() + "-" + fileName;

        // Presigned URL 요청 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(uniqueFileName)  // 파일 이름을 지정
                .contentType(contentType) // 파일 MIME 타입
                .build();

        // URL 만료 시간 설정
        Duration expiration = Duration.ofMinutes(10); // 10분

        // PresignedPutObjectRequest 생성 (Presigner를 사용하여 Presigned URL을 생성)
        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(
                presignRequest -> presignRequest.putObjectRequest(putObjectRequest)
                        .signatureDuration(expiration)
        );

        // URL 반환
        S3FileUploadResponse response = new S3FileUploadResponse();
        response.setPresignedUrl(presignedPutObjectRequest.url().toString());
        // S3 파일 path
        response.setDownloadPath(bucket + "/" + uniqueFileName);
        return response;
    }

    /**
     * MIME 타입 & 확장자 검사
     */
    private void verifyMimeType(String contentType, String fileName) {

        // 파일 확장자
        String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();

        if (!isValidMimeType(contentType, fileExtension)) {
            throw new PlayHiveException(ErrorCode.INVALID_MIME_TYPE);
        }

    }

    private boolean isValidMimeType(String contentType, String fileExtension) {
        for (ContentType type : ContentType.values()) {
            if (type.getValue().equals(contentType)) {
                // MIME 타입에서 / 뒤에 있는 확장자 부분을 추출
                String mimeExtension = contentType.split("/")[1].toLowerCase();

                // 파일 확장자와 비교
                if (mimeExtension.equals(fileExtension)) {
                    return true;
                }
            }
        }
        return false;
    }
}