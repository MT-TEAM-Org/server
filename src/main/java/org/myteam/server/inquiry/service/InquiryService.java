package org.myteam.server.inquiry.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.admin.utill.AdminBotCreateLogEvent;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.service.CommentService;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryCount;
import org.myteam.server.inquiry.repository.InquiryCountRepository;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.util.slack.service.SlackService;
import org.springframework.context.ApplicationEventPublisher;
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

    private final InquiryRepository inquiryRepository;
    private final InquiryCountRepository inquiryCountRepository;
    private final SlackService slackService;
    private final SecurityReadService securityReadService;
    private final InquiryReadService inquiryReadService;
    private final MemberJpaRepository memberJpaRepository;
    private final CommentService commentService;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 문의 내역 생성
     * @param content
     * @param clientIP
     * @return
     */
    public String createInquiry(String content, String email, String clientIP) {
        UUID loginUser = securityReadService.getAuthenticatedPublicId();
        Optional<Member> member = Optional.empty();
        if (loginUser != null) {
            member = memberJpaRepository.findByPublicId(loginUser);
        }

        if (member.isEmpty() && email == null) {
            throw new PlayHiveException(ErrorCode.INQUIRY_EMAIL_EMPTY);
        }

        Inquiry inquiry = makeInquiry(content, clientIP, member, email);
        InquiryCount inquiryCount = InquiryCount.createCount(inquiry);

        String slackMessage = getSlackMessage(content, clientIP, member, email);

        inquiry=inquiryRepository.save(inquiry);
        inquiryCountRepository.save(inquiryCount);
        AdminBotCreateLogEvent adminBotCreateLogEvent=AdminBotCreateLogEvent
                .builder()
                .contentId(inquiry.getId())
                .staticDataType(StaticDataType.Inquiry)
                .isMember(member.isPresent())
                .build();
        applicationEventPublisher.publishEvent(adminBotCreateLogEvent);
        slackService.sendSlackNotification(slackMessage);

        log.info("문의 내용이 접수되었습니다.");

        return inquiry.getContent();
    }

    /**
     * 문의 내역 삭제
     * @param inquiryId
     */
    public void deleteInquiry(final Long inquiryId) {
        log.info("문의내역: {} 삭제 요청", inquiryId);
        Member member = securityReadService.getMember();
        Inquiry inquiry = inquiryReadService.findInquiryById(inquiryId);

        inquiry.verifyInquiryAuthor(member);

        inquiryCountRepository.deleteByInquiryId(inquiry.getId());
        inquiryRepository.delete(inquiry);

        log.info("문의내역: {} 삭제", inquiryId);
        commentService.deleteCommentByPost(CommentType.INQUIRY, inquiryId);
    }

    private Inquiry makeInquiry(String content, String clientIP, Optional<Member> member, String email) {
        Inquiry inquiry = Inquiry.builder()
                .content(content)
                .member(member.isEmpty() ? null : member.get())
                .clientIp(clientIP)
                .createdAt(LocalDateTime.now())
                .email(member.isEmpty() ? email : null)
                .build();
        return inquiry;
    }

    private String getSlackMessage(String content, String clientIP, Optional<Member> member, String email) {
        String slackMessage = String.format(
                "📩 새로운 문의가 접수되었습니다!\n\n" +
                        "🔹 문의 내용: %s\n" +
                        "🔹 작성자: %s (%s)\n" +
                        "🔹 요청 IP: %s\n" +
                        "🔹 작성 시간: %s\n\n" +
                        "확인 후 답변해 주세요. ✅",
                content,
                member.map(Member::getNickname).orElse("익명 사용자"),
                member.map(Member::getEmail).orElse(email),
                clientIP,
                LocalDateTime.now()
        );
        return slackMessage;
    }

}
