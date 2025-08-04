package org.myteam.server.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.admin.dto.request.MemberSearchRequestDto;
import org.myteam.server.admin.dto.response.MemberSearchResponseDto;
import org.myteam.server.admin.service.AdminMemberSearchService;
import org.myteam.server.global.exception.ErrorResponse;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.global.web.response.ResponseStatus;
import org.myteam.server.member.dto.MemberDeleteRequest;
import org.myteam.server.member.dto.MemberGetRequest;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.dto.MemberStatusUpdateRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.myteam.server.admin.dto.request.MemberSearchRequestDto.*;
import static org.myteam.server.admin.dto.response.MemberSearchResponseDto.*;
import static org.myteam.server.global.security.jwt.JwtProvider.HEADER_AUTHORIZATION;
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
    private final AdminMemberSearchService adminMemberSearchService;

    /*@Operation(summary = "이메일로 회원 조회", description = "관리자가 특정 이메일을 가진 회원을 조회합니다.")
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
    }*/

    @Operation(summary = "회원 정보들을 조회", description = "각종 조건을 바탕으로 회원 정보들을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/list")
    public ResponseEntity<ResponseDto<Page<ResponseMemberSearch>>>
    getMemberDataList(@RequestBody @Valid RequestMemberSearch requestMemberSearch, BindingResult bindingResult) {

        return ResponseEntity.ok(
                new ResponseDto<>(org.myteam.server.global.web.response.ResponseStatus.SUCCESS.name(), "성공",
                        adminMemberSearchService.getMemberDataList(requestMemberSearch)));

    }
    @Operation(summary = "한 회원의 정보 조회", description = "관리자가 publicId로 특정 회원의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/publicId")
    public ResponseEntity<ResponseDto<ResponseMemberDetail>>
    getMemberDataList(@RequestBody @Valid RequestMemberDetail requestMemberDetail, BindingResult bindingResult) {

        return ResponseEntity.ok(
                new ResponseDto<>(org.myteam.server.global.web.response.ResponseStatus.SUCCESS.name(), "성공",
                        adminMemberSearchService.getMemberDetailData(requestMemberDetail))
        );

    }

    @Operation(summary = "회원의 신고 사항 불러오기", description = "관리자가 특정 회원에 대해서 접수된 신고들을 불러옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reports")
    public ResponseEntity<ResponseDto<Page<ResponseReportList>>>
    getMemberReportedList(@RequestBody @Valid RequestMemberDetail requestMemberDetail, BindingResult bindingResult) {

        return ResponseEntity.ok(
                new ResponseDto<>(ResponseStatus.SUCCESS.name(), "성공"
                        , adminMemberSearchService.getMemberReportedList(requestMemberDetail))
        );
    }

    @Operation(summary = "회원의 상태 업데이트 및 메모추가",
            description = "관리자가 특정 회원의 상태를 업데이트 및 메모를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestBody @Valid MemberStatusUpdateRequest memberStatusUpdateRequest,
                                          BindingResult bindingResult,
                                          HttpServletRequest httpServletRequest) {
        log.info("AdminController updateStatus 메서드 실행");
        String authorizationHeader = httpServletRequest.getHeader(HEADER_AUTHORIZATION);

        // accessToken 으로 부터 유저 정보 반환
        MemberResponse response = memberReadService.getAuthenticatedMember(authorizationHeader);

        log.info("email : {}", response.getEmail());

        // 서비스 호출
        String targetEmail = response.getEmail(); // 변경을 시도하는 유저의 이메일 (본인 또는 관리자)

        memberService.updateStatus(targetEmail, memberStatusUpdateRequest);

        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "회원 상태가 성공적으로 변경되었습니다.", null));
    }

}
