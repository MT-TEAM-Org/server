package org.myteam.server.upload.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class S3FileUploadResponse {
    private String presignedUrl;
    private String downloadUrl;

    private S3FileUploadResponse(String presignedUrl, String downloadUrl) {
        this.presignedUrl = presignedUrl;
        this.downloadUrl = downloadUrl;
    }
    
    public static S3FileUploadResponse createResponse(String presignedUrl, String downloadUrl) {
        return new S3FileUploadResponse(presignedUrl, downloadUrl);
    }
}