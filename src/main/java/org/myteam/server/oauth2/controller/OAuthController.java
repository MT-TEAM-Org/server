package org.myteam.server.oauth2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.member.service.MemberService;
import org.myteam.server.oauth2.dto.AddMemberInfoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
@Tag(name = "OAUTH", description = "oauth 시 추가 정보 기입하는 API")
public class OAuthController {

    private final MemberService memberService;

    @Operation(summary = "추가정보", description = "사용자의 추가정보를 입력합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "입력값 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "아이디 중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/add-info")
    public ResponseEntity<ResponseDto<Void>> addMemberInfo(@Valid @RequestBody AddMemberInfoRequest request) {

        memberService.addInfo(request);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "oauth 멤버 정보 수정",
                null
        ));

    }
}
