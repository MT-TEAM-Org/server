package org.myteam.server.upload.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class S3UploadRequest {
    String fileName;
    String contentType;
}
