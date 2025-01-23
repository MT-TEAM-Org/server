package org.myteam.server.member.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.member.controller.response.Oauth2MemberResponse;
import org.myteam.server.member.domain.validator.MemberValidator;
import org.myteam.server.member.service.Oauth2MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.myteam.server.global.exception.ErrorCode.INVALID_PARAMETER;
import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/oauth2/members")
@RequiredArgsConstructor
public class Oauth2MemberController {
    private final Oauth2MemberService oauth2MemberService;
    private final JwtProvider jwtProvider;

    /**
     * 이메일로 사용자 조회
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable String email) {
        log.info("Oauth2MemberController getByEmail 메서드 실행 : {}", email);

        // 이메일 유효성 검사
        if (MemberValidator.validateEmail(email) == null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("email", "이메일 형식으로 작성해주세요");
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(
                            INVALID_PARAMETER.getStatus(),
                            INVALID_PARAMETER.getMsg(),
                            errorMap
                    )
            );
        }

        Oauth2MemberResponse response = oauth2MemberService.getByEmail(email);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "사용자 조회 성공", response));
    }
}
