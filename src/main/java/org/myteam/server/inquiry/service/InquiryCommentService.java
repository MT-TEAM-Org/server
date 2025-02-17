package org.myteam.server.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryComment;
import org.myteam.server.inquiry.dto.request.InquiryCommentRequest;
import org.myteam.server.inquiry.dto.response.InquiryCommentResponse;
import org.myteam.server.inquiry.repository.InquiryCommentRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.upload.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InquiryCommentService {

    private final InquiryReadService inquiryReadService;
    private final SecurityReadService securityReadService;
    private final BadWordFilter badWordFilter;
    private final InquiryCommentRepository inquiryCommentRepository;
    private final InquiryCountService inquiryCountService;
    private final InquiryCommentReadService inquiryCommentReadService;
    private final S3Service s3Service;

    /**
     * 문의 내역 댓글 생성
     * TODO: 익명 사용자 확인해보기
     * @param inquiryId
     * @param request
     * @param createdIp
     * @return
     */
    public InquiryCommentResponse save(Long inquiryId, InquiryCommentRequest request, String createdIp) {
        Inquiry inquiry = inquiryReadService.findInquiryById(inquiryId);
        Member member = securityReadService.getMember();

        InquiryComment inquiryComment = InquiryComment.createComment(inquiry, member, request.getImageUrl(),
                badWordFilter.filterMessage(request.getComment()), createdIp);

        inquiryCommentRepository.save(inquiryComment);

        inquiryCountService.addCommentCount(inquiry.getId());

        return InquiryCommentResponse.createResponse(inquiryComment, member);
    }

    public void deleteInquiryComment(Long inquiryCommentId) {
        Member member = securityReadService.getMember();
        InquiryComment inquiryComment = inquiryCommentReadService.findById(inquiryCommentId);

        verifyInquiryCommentAuthor(inquiryComment, member);

        s3Service.deleteFile(getImagePath(inquiryComment.getImageUrl()));
        inquiryCommentRepository.deleteById(inquiryCommentId);

        inquiryCountService.minusCommendCount(inquiryComment.getInquiry().getId());
    }

    /**
     * path만 추출
     * TODO :: 운영에선 버킷 이름 수정 예정
     */
    public static String getImagePath(String url) {
        String target = "devbucket/";
        int index = url.indexOf(target);
        if (index != -1) {
            return url.substring(index + target.length());
        }
        return null;
    }

    /**
     * 기존 이미지와 요청 이미지가 같지 않으면 삭제
     */
    private void verifyBoardCommentImageAndRequestImage(String boardCommentImageUrl, String requestImageUrl) {
        if (!boardCommentImageUrl.equals(requestImageUrl)) {
            s3Service.deleteFile(getImagePath(requestImageUrl));
        }
    }

    /**
     * 작성자와 일치 하는지 검사 (어드민도 수정/삭제 허용)
     */
    private void verifyInquiryCommentAuthor(InquiryComment inquiryComment, Member member) {
        if (!inquiryComment.isAuthor(member) && !member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
        }
    }
}
