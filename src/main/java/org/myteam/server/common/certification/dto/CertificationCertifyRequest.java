package org.myteam.server.common.certification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CertificationCertifyRequest {
    @Pattern(regexp = "^[0-9a-zA-Z_]+@[0-9a-zA-Z]+(\\.[a-zA-Z]{2,3}){1,2}$", message = "이메일 형식으로 작성해주세요")
    private String email;
    @NotBlank
    private String code;
}
