package org.myteam.server.upload.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.upload.controller.request.S3UploadRequest;
import org.myteam.server.upload.controller.response.S3FileUploadResponse;
import org.myteam.server.upload.service.AwsS3Service;
import org.myteam.server.upload.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
@Slf4j
@Tag(name = "S3 관련 API", description = "파일 업로드를 위한 S3 관련 API")
public class S3Controller {

    // minIO 로컬용 S3 service
    private final S3Service s3Service;
    // AWS S3 운영용 service
    private final AwsS3Service awsS3Service;

    /**
     * Presigned URL 생성
     */
    @Operation(summary = "PreginedUrl 생성", description = "PreginedUrl을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PreginedUrl 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "s3 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping
    public ResponseEntity<ResponseDto<S3FileUploadResponse>> generatePresignedUrl(
            @ModelAttribute S3UploadRequest request) {

        // minio Presigned URL 생성
        // S3FileUploadResponse response = s3Service.generatePresignedUrl(request.getFileName(), request.getContentType());

        // AWS Presigned URL 생성
        S3FileUploadResponse response = awsS3Service.generatePresignedUrl(request.getFileName(),
                request.getContentType());

        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "Presigned URL 생성 성공", response));
    }

    /**
     * 파일 삭제 API
     *
     * @param fileName 삭제할 파일의 전체 경로 ex) image/8be43abc-455f-4c4a-9458-87f6a20d3008-son.jpeg
     */
    @Operation(summary = "s3 파일 삭제", description = "s3에 업로드 되어있는 파일을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파일 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "s3 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto<String>> deleteFile(@RequestParam String fileName) {
        //  minio S3 삭제
        //  s3Service.deleteFile(fileName);

        // AWS S3 삭제
        awsS3Service.deleteFile(fileName);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "파일 삭제 성공", null));
    }
}
