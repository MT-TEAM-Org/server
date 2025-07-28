package org.myteam.server.member.controller;

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
import org.myteam.server.member.dto.MemberDeleteRequest;
import org.myteam.server.member.dto.MemberGetRequest;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
@Tag(name = "관리자 회원 관리 API", description = "관리자가 회원을 관리할 수 있는 API")
@PreAuthorize("hasAuthority(T(org.myteam.server.member.domain.MemberRole).ADMIN.name())")
public class AdminController {

    private final MemberReadService memberReadService;
    private final MemberService memberService;

    @Operation(summary = "이메일로 회원 조회", description = "관리자가 특정 이메일을 가진 회원을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/email")
    public ResponseEntity<?> getByEmail(@Valid MemberGetRequest memberGetRequest, BindingResult bindingResult) {
        log.info("MemberController getByEmail 메서드 실행 : {}", memberGetRequest);
        MemberResponse response = memberReadService.getByEmail(memberGetRequest.getEmail());
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "회원 정보 조회 성공", response), HttpStatus.OK);
    }

    @Operation(summary = "닉네임으로 회원 조회", description = "관리자가 특정 닉네임을 가진 회원을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/nickname")
    public ResponseEntity<?> getByNickname(@Valid MemberGetRequest memberGetRequest, BindingResult bindingResult) {
        log.info("MemberController getByNickname 메서드 실행 : {}", memberGetRequest);
        MemberResponse response = memberReadService.getByNickname(memberGetRequest.getNickname());
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "회원 정보 조회 성공", response), HttpStatus.OK);
    }

    @Operation(summary = "회원 목록 조회", description = "관리자가 모든 회원 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<?> list() {
        log.info("getAllMembers : 회원 정보 목록 조회 메서드 실행");
        List<Member> allMembers = memberReadService.list();
        List<MemberResponse> response = allMembers.stream().map(MemberResponse::new).toList();
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "회원 정보 목록 조회 성공", response), HttpStatus.OK);
    }

    @Deprecated
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto<String>> delete(@RequestBody MemberDeleteRequest memberDeleteRequest, BindingResult bindingResult) {
        log.info("MemberController delete 메서드 실행 : {}", memberDeleteRequest);
        String email = memberDeleteRequest.getEmail();
        memberService.delete(email);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "회원 삭제 성공",
                null
        ));
    }
}
