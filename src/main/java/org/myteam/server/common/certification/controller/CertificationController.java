package org.myteam.server.common.certification.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.dto.CertificationCertifyRequest;
import org.myteam.server.common.certification.dto.CertificationEmailRequest;
import org.myteam.server.common.certification.service.CertificationService;
import org.myteam.server.common.mail.domain.EmailType;
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
public class CertificationController {
    private final CertificationService certificationService;

    @PostMapping("/send")
    public ResponseEntity<?> sendCertificationEmail(@Valid @RequestBody CertificationEmailRequest certificationEmailRequest, BindingResult bindingResult) {
        log.info("send-certification email: {}", certificationEmailRequest.getEmail());
        certificationService.send(certificationEmailRequest.getEmail(), EmailType.CERTIFICATION);
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "인증 코드 이메일 전송 성공", null), HttpStatus.OK);
    }

    @PostMapping("/certify-code")
    public ResponseEntity<?> certifyCode(@Valid @RequestBody CertificationCertifyRequest certificationCertifyRequest,
                                         HttpSession session,
                                         BindingResult bindingResult) {
        String code = certificationCertifyRequest.getCode(); // 인증 코드
        String email = certificationCertifyRequest.getEmail(); // 이메일
        boolean isValid = certificationService.certify(email, code);

        if (isValid) {
            log.info("certify email: {} success", email);

            session.setAttribute("certifiedEmail", email);
            return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "인증 코드 확인", null), HttpStatus.OK);
        } else {
            log.info("certify code failed");
            log.info("email: {}, code: {}", email, code);
            throw new PlayHiveException(UNAUTHORIZED);
        }
    }
}
