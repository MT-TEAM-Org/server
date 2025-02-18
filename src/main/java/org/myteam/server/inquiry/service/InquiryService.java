package org.myteam.server.inquiry.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.service.BoardCountReadService;
import org.myteam.server.board.service.BoardRecommendReadService;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.inquiry.repository.InquiryCountRepository;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.util.ClientUtils;
import org.myteam.server.util.slack.service.SlackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class InquiryService {

    private final MemberRepository memberRepository;
    private final InquiryRepository inquiryRepository;
    private final InquiryCountRepository inquiryCountRepository;
    private final SlackService slackService;
    private final InquiryCountReadService inquiryCountReadService;
    private final InquiryRecommendReadService inquiryRecommendReadService;
    private final SecurityReadService securityReadService;
    private final InquiryReadService inquiryReadService;
    private final MemberReadService memberReadService;

    /**
     * 문의 내역 생성
     * @param content
     * @param memberPublicId
     * @param clientIP
     * @return
     */
    public String createInquiry(String content, UUID memberPublicId, String clientIP) {
        Optional<Member> member = memberRepository.findByPublicId(memberPublicId);

        Inquiry inquiry = makeInquiry(content, clientIP, member);
        InquiryCount inquiryCount = InquiryCount.createCount(inquiry);

        String slackMessage = getSlackMessage(content, clientIP, member);

        inquiryRepository.save(inquiry);
        inquiryCountRepository.save(inquiryCount);
        boolean isRecommended = inquiryRecommendReadService.isRecommended(inquiry.getId(), memberPublicId);
        slackService.sendSlackNotification(slackMessage);

        log.info("문의 내용이 접수되었습니다.");

        return inquiry.getContent();
    }

    /**
     * 문의 내역 삭제
     * TODO: 익명일 때 생각하기
     * @param inquiryId
     */
    public void deleteInquiry(final Long inquiryId) {

        UUID loginUser = securityReadService.getMember().getPublicId();

        Member member = memberReadService.findById(loginUser);
        Inquiry inquiry = inquiryReadService.findInquiryById(inquiryId);

        inquiry.verifyInquiryAuthor(member);

        inquiryCountRepository.deleteByInquiryId(inquiry.getId());
        inquiryRepository.delete(inquiry);
    }

    private Inquiry makeInquiry(String content, String clientIP, Optional<Member> member) {
        Inquiry inquiry = Inquiry.builder()
                .content(content)
                .member(member.orElse(null))
                .clientIp(clientIP)
                .createdAt(LocalDateTime.now())
                .build();
        return inquiry;
    }

    private String getSlackMessage(String content, String clientIP, Optional<Member> member) {
        String slackMessage = String.format(
                "📩 새로운 문의가 접수되었습니다!\n\n" +
                        "🔹 문의 내용: %s\n" +
                        "🔹 작성자: %s (%s)\n" +
                        "🔹 요청 IP: %s\n" +
                        "🔹 작성 시간: %s\n\n" +
                        "확인 후 답변해 주세요. ✅",
                content,
                member.map(Member::getNickname).orElse("익명 사용자"),
                member.map(Member::getEmail).orElse("익명"),
                clientIP,
                LocalDateTime.now()
        );
        return slackMessage;
    }

}
