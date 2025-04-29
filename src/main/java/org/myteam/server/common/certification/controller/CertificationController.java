package org.myteam.server.common.certification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.dto.CertificationCertifyRequest;
import org.myteam.server.common.certification.dto.CertificationEmailRequest;
import org.myteam.server.common.certification.service.CertificationService;
import org.myteam.server.common.mail.domain.EmailType;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.myteam.server.global.exception.ErrorCode.UNAUTHORIZED;
import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certification")
@Tag(name = "이메일 인증 API", description = "이메일 인증 코드 전송 및 검증 API")
public class CertificationController {
    private final CertificationService certificationService;

    @Operation(summary = "인증 코드 전송", description = "입력한 이메일로 인증 코드를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "이메일 생성 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "이메일 전송 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/send")
    public ResponseEntity<?> sendCertificationEmail(@Valid @RequestBody CertificationEmailRequest certificationEmailRequest, BindingResult bindingResult) {
        log.info("send-certification email: {}", certificationEmailRequest.getEmail());
        certificationService.send(certificationEmailRequest.getEmail());
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "인증 코드 이메일 전송 성공", null), HttpStatus.OK);
    }

    @Operation(summary = "인증 코드 검증", description = "사용자가 입력한 인증 코드가 유효한지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 코드 검증 성공"),
            @ApiResponse(responseCode = "401", description = "인증 코드가 유효하지 않음"),
            @ApiResponse(responseCode = "500", description = "이메일 타입 오")
    })
    @PostMapping("/certify-code")
    public ResponseEntity<?> certifyCode(@Valid @RequestBody CertificationCertifyRequest certificationCertifyRequest,
                                         BindingResult bindingResult) {
        String code = certificationCertifyRequest.getCode(); // 인증 코드
        String email = certificationCertifyRequest.getEmail(); // 이메일
        boolean isValid = certificationService.certify(email, code);

        if (isValid) {
            log.info("certify email: {} success", email);
            return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "인증 코드 확인", null), HttpStatus.OK);
        } else {
            log.info("certify code failed");
            log.info("email: {}, code: {}", email, code);
            throw new PlayHiveException(UNAUTHORIZED);
        }
    }
}
