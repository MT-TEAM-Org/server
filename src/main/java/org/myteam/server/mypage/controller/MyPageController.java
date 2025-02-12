package org.myteam.server.mypage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.dto.reponse.BoardListResponse;
import org.myteam.server.board.dto.request.BoardServiceRequest;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.inquiry.dto.request.InquirySearchRequest;
import org.myteam.server.inquiry.dto.response.InquiriesListResponse;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.mypage.dto.request.MyPageRequest.BoardRequest;
import org.myteam.server.mypage.dto.response.MyPageResponse.MemberStatsResponse;
import org.myteam.server.mypage.service.MyPageReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-page")
public class MyPageController {

    private MyPageReadService myPageReadService;
    private InquiryReadService inquiryReadService;

    @GetMapping
    public ResponseEntity<ResponseDto<MemberStatsResponse>> getMyPage() {
        MemberStatsResponse response = myPageReadService.getMemberInfo();

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "회원 정보가 조회되었습니다.",
                response
        ));
    }

    @GetMapping("/board")
    public ResponseEntity<ResponseDto<BoardListResponse>> getMyBoard(@ModelAttribute BoardServiceRequest boardRequest) {
        BoardListResponse memberPosts = myPageReadService.getMemberPosts(boardRequest);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "내가 쓴 게시물이 조회되었습니다.",
                memberPosts
        ));
    }

    @GetMapping("/inquiry")
    public ResponseEntity<ResponseDto<InquiriesListResponse>> getMyInquiry(@ModelAttribute @Valid InquirySearchRequest request) {
        InquiriesListResponse inquiriesListResponse = inquiryReadService.getInquiriesByMember(request);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "내가 쓴 문의내역이 조회되었습니다.",
                inquiriesListResponse
        ));
    }


}
