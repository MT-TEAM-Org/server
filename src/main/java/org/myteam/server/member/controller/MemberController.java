package org.myteam.server.member.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.dto.MemberDeleteRequest;
import org.myteam.server.member.dto.MemberResponse;
import org.myteam.server.member.dto.MemberUpdateRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable String email) {
        log.info("MemberController getByEmail 메서드 실행 : {}", email);
        Member member = memberService.getByEmail(email);
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "회원 정보 조회 성공", new MemberResponse(member)), HttpStatus.OK);
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<?> getByNickname(@PathVariable String nickname) {
        log.info("MemberController getByNickname 메서드 실행 : {}", nickname);
        Optional<Member> memberOP = memberService.findByNickname(nickname);
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "회원 정보 조회 성공", memberOP.isPresent() ? new MemberResponse(memberOP.get()) : null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> list() {
        log.info("getAllMembers : 회원 정보 목록 조회 메서드 실행");
        List<Member> allMembers = memberService.list();
        List<MemberResponse> response = allMembers.stream().map(MemberResponse::new).toList();
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "회원 정보 목록 조회 성공", response), HttpStatus.OK);
    }

    @Deprecated
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody @Valid MemberUpdateRequest memberUpdateRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("MemberController update 메서드 실행 : {}", memberUpdateRequest.toString());
        String email = memberService.getCurrentLoginUserEmail(userDetails.getPublicId()); // 현재 로그인한 사용자 이메일
        MemberResponse response = memberService.update(email, memberUpdateRequest);
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "회원정보 수정 성공", response), HttpStatus.OK);
    }

    @Deprecated
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<?> delete(@PathVariable String email, @RequestBody MemberDeleteRequest memberDeleteRequest) {
        log.info("MemberController delete( 메서드 실행 : {}, {}", email, memberDeleteRequest);
        memberService.delete(email, memberDeleteRequest.getPassword());
        return new ResponseEntity<>(new ResponseDto<>(SUCCESS.name(), "회원 삭제 성공", null), HttpStatus.OK);
    }

    @PutMapping("/email/{email}/status/{status}")
    public ResponseEntity<?> updateStatus(@PathVariable String email, @PathVariable String status) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(status)) {
            throw new PlayHiveException("유저 상태 변경 실패 (유효성 에러). email : " + email +", status : " + status);
        }

        // 서비스 호출
        MemberStatus memberStatus = MemberStatus.valueOf(status.toUpperCase());
        memberService.updateStatus(email, memberStatus);

        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "회원 상태가 성공적으로 변경되었습니다.", null));
    }
}
