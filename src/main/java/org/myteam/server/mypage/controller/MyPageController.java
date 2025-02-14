package org.myteam.server.mypage.controller;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.dto.reponse.BoardListResponse;
import org.myteam.server.board.dto.request.BoardSearchRequest;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.inquiry.dto.request.InquirySearchRequest;
import org.myteam.server.inquiry.dto.response.InquiriesListResponse;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.mypage.dto.request.MyPageRequest.MyPageUpdateRequest;
import org.myteam.server.mypage.dto.response.MyPageResponse.MemberModifyResponse;
import org.myteam.server.mypage.dto.response.MyPageResponse.MemberStatsResponse;
import org.myteam.server.mypage.service.MyPageReadService;
import org.myteam.server.mypage.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-page")
public class MyPageController {

    private final MyPageReadService myPageReadService;
    private final MyPageService myPageService;
    private final InquiryReadService inquiryReadService;

    @GetMapping
    public ResponseEntity<ResponseDto<MemberStatsResponse>> getMyPage() {
        MemberStatsResponse response = myPageReadService.getMemberInfo();

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "회원 정보가 조회되었습니다.",
                response
        ));
    }

    @GetMapping("/modify")
    public ResponseEntity<ResponseDto<MemberModifyResponse>> getMyInfo() {
        MemberModifyResponse response = myPageReadService.getMemberAllInfo();

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "내 정보 수정 조회내역입니다.",
                response
        ));
    }

    @PutMapping("/modify")
    public ResponseEntity<ResponseDto<String>> updateMyInfo(@RequestBody MyPageUpdateRequest myPageUpdateRequest) {
        myPageService.updateMemberInfo(myPageUpdateRequest);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "회원 정보가 수정되었습니다.",
                null
        ));
    }

    @GetMapping("/board")
    public ResponseEntity<ResponseDto<BoardListResponse>> getMyBoard(
            @Valid @ModelAttribute BoardSearchRequest boardSearchRequest) {
        BoardListResponse memberPosts = myPageReadService.getMemberPosts(boardSearchRequest.toServiceRequest());

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "내가 쓴 게시물이 조회되었습니다.",
                memberPosts
        ));
    }

    @GetMapping("/inquiry")
    public ResponseEntity<ResponseDto<InquiriesListResponse>> getMyInquiry(
            @Valid @ModelAttribute InquirySearchRequest request
    ) {
        InquiriesListResponse inquiriesListResponse = inquiryReadService.getInquiriesByMember(
                request.toServiceRequest());

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "내가 쓴 문의내역이 조회되었습니다.",
                inquiriesListResponse
        ));
    }

}
