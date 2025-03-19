package org.myteam.server.upload.service;

import org.myteam.server.upload.controller.response.S3FileUploadResponse;

public interface StorageService {
    /**
     * Presigned URL 생성
     *
     * @param fileName    업로드할 파일의 이름
     * @param contentType 파일의 MIME 타입
     * @return Presigned URL과 다운로드 URL을 포함한 응답
     */
    S3FileUploadResponse generatePresignedUrl(String fileName, String contentType);

    /**
     * 파일 삭제
     *
     * @param fileName 삭제할 파일의 전체 경로
     * @return 파일 삭제 성공 여부
     */
    boolean deleteFile(String fileName);
}