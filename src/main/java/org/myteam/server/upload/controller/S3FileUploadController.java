package org.myteam.server.upload.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.upload.controller.response.S3FileUploadResponse;
import org.myteam.server.upload.service.S3PresignedUrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class S3FileUploadController {

    private final S3PresignedUrlService s3PresignedUrlService;

    /**
     * Presigned URL 생성
     * @param fileName    업로드할 파일의 이름
     * @param contentType 파일의 MIME 타입 (예: image/png)
     */
    @GetMapping("/upload")
    public ResponseEntity<ResponseDto<S3FileUploadResponse>> generatePresignedUrl(
            @RequestParam("contentType") String contentType,
            @RequestParam("fileName") String fileName) {

        // Presigned URL 생성
        S3FileUploadResponse response = s3PresignedUrlService.generatePresignedUrl(fileName, contentType);

        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "Presigned URL 생성 성공", response));
    }
}
