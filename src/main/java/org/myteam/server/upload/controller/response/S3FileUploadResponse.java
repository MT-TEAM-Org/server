package org.myteam.server.upload.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class S3FileUploadResponse {
    private String presignedUrl;
    private String downloadPath;
}
