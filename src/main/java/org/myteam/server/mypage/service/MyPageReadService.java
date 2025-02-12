package org.myteam.server.mypage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.dto.reponse.BoardListResponse;
import org.myteam.server.board.dto.request.BoardServiceRequest;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.inquiry.dto.request.InquiryFindRequest;
import org.myteam.server.inquiry.dto.request.InquirySearchRequest;
import org.myteam.server.inquiry.dto.response.InquiriesListResponse;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.mypage.dto.request.MyPageRequest.BoardRequest;
import org.myteam.server.mypage.dto.response.MyPageResponse.MemberStatsResponse;
import org.myteam.server.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageReadService {

    private final SecurityReadService securityReadService;
    private final BoardReadService boardReadService;
    private final InquiryReadService inquiryReadService;

    /**
     * TODO: 보드 전체 조회
     * TODO: 댓글은 어떻게 구현할지 생각해봐야할듯
     * @return
     */
    public MemberStatsResponse getMemberInfo() {
        Member member = securityReadService.getMember();
        UUID publicId = member.getPublicId();

        int postCount = boardReadService.getMyBoardListCount(publicId);
        int commentCount = 0;
        int inquiryCount = inquiryReadService.getInquiriesCountByMember(publicId);

        return MemberStatsResponse.createResponse(member, postCount, commentCount, inquiryCount);
    }

    public BoardListResponse getMemberPosts(BoardServiceRequest boardServiceRequest) {
        Member member = securityReadService.getMember();
        return boardReadService.getMyBoardList(boardServiceRequest, member.getPublicId());
    }

    public InquiriesListResponse getMemberInquires(InquirySearchRequest inquiryFindRequest) {
        return inquiryReadService.getInquiriesByMember(inquiryFindRequest);
    }
}
