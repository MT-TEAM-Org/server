package org.myteam.server.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.myteam.server.auth.dto.AuthResponse;
import org.myteam.server.global.exception.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;



@Tag(name = "인증 API", description = "회원가입 및 로그인 관련 API")
public class AdminLoginController {
    @Operation(summary = "관리자 로그인", description = "관리자가 로그인을 하여 토큰을 받습니다." +
            "계정은 username으로 비밀번호는 password를 프로퍼티로 보내주시면됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "JSON 파싱 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("api/admin/login")
    public void adminLoginController(){
    }
}
