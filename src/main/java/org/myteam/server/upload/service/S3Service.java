package org.myteam.server.upload.service;

import jakarta.validation.ValidationException;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.myteam.server.upload.controller.response.S3FileUploadResponse;
import org.myteam.server.upload.domain.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    @Value("${aws.bucket}")
    private String bucket;
    private final static String downloadUrl = "https://media.playhive.co.kr/";

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

        try {
            // Presigned URL 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(uniqueFileName)  // 파일 이름을 지정
                    .contentType(contentType) // 파일 MIME 타입
                    .build();

            // URL 만료 시간 설정
            Duration expiration = Duration.ofMinutes(5);

            // PresignedPutObjectRequest 생성 (Presigner를 사용하여 Presigned URL을 생성)
            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(
                    presignRequest -> presignRequest.putObjectRequest(putObjectRequest)
                            .signatureDuration(expiration)
            );

            S3FileUploadResponse response = S3FileUploadResponse.createResponse(
                    presignedPutObjectRequest.url().toString(),
                    downloadUrl + uniqueFileName
            );

            return response;

        } catch (Exception e) {
            log.error("S3 Presigned URL 생성 실패: {}", e.getMessage(), e);
            throw new RuntimeException("S3 URL 생성 실패");
        }
    }

    /**
     * 파일 삭제
     */
    public boolean deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            log.warn("파일 삭제 요청이 들어왔지만, fileName이 null 또는 빈 문자열입니다.");
            return false;
        }

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            return true;
        } catch (S3Exception e) {
            log.error("S3 파일 삭제 실패: {}", e.awsErrorDetails().errorMessage(), e);
            throw new RuntimeException("S3 URL 삭제 실패");
        }
    }

    /**
     * MIME 타입 & 확장자 검사
     */
    private void verifyMimeType(String contentType, String fileName) {

        // 파일 확장자
        String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();

        if (!isValidMimeType(contentType, fileExtension)) {
            String errorMsg = String.format("유효하지 않은 MIME 타입 또는 확장자입니다. contentType=%s, extension=%s", contentType,
                    fileExtension);
            log.warn(errorMsg);
            throw new ValidationException(errorMsg);
        }
    }

    /**
     * 유효한 MIME 타입 및 확장자 체크
     */
    private boolean isValidMimeType(String contentType, String fileExtension) {
        for (MediaType type : MediaType.values()) {
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