package org.myteam.server.inquiry.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberRepository;
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
    private final SlackService slackService;

    public String createInquiry(String content, UUID memberPublicId, String clientIP) {
        Optional<Member> member = memberRepository.findByPublicId(memberPublicId);

        Inquiry inquiry = Inquiry.builder()
                .content(content)
                .member(member.orElse(null))
                .clientIp(clientIP)
                .createdAt(LocalDateTime.now())
                .build();

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

        inquiryRepository.save(inquiry);
        slackService.sendSlackNotification(slackMessage);

        log.info("문의 내용이 접수되었습니다.");

        return inquiry.getContent();
    }
}
