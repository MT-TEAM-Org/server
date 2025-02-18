//package org.myteam.server.upload.controller;
//
//import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.myteam.server.global.web.response.ResponseDto;
//import org.myteam.server.upload.controller.request.S3UploadRequest;
//import org.myteam.server.upload.controller.response.S3FileUploadResponse;
//import org.myteam.server.upload.service.S3Service;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/upload")
//@Slf4j
//public class S3Controller {
//
//    private final S3Service s3Service;
//
//    /**
//     * Presigned URL 생성
//     */
//    @GetMapping
//    public ResponseEntity<ResponseDto<S3FileUploadResponse>> generatePresignedUrl(
//            @ModelAttribute S3UploadRequest request) {
//
//        // Presigned URL 생성
//        S3FileUploadResponse response = s3Service.generatePresignedUrl(request.getFileName(), request.getContentType());
//
//        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "Presigned URL 생성 성공", response));
//    }
//
//    /**
//     * 파일 삭제 API
//     *
//     * @param fileName 삭제할 파일의 전체 경로 ex) image/8be43abc-455f-4c4a-9458-87f6a20d3008-son.jpeg
//     */
//    @DeleteMapping("/delete")
//    public ResponseEntity<ResponseDto<String>> deleteFile(@RequestParam String fileName) {
//        s3Service.deleteFile(fileName);
//        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "파일 삭제 성공", null));
//    }
//}
