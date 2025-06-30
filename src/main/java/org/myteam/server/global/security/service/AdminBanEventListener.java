package org.myteam.server.global.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.mail.core.MailStrategy;
import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.common.certification.mail.factory.MailStrategyFactory;
import org.myteam.server.common.certification.mail.strategy.NotifySuspendStrategy;
import org.myteam.server.common.certification.service.CertificationService;
import org.myteam.server.global.security.dto.AdminBanEvent;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.dto.MemberStatusUpdateRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.MemberService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminBanEventListener {

    private final MemberService memberService;
    private final MemberReadService memberReadService;


    @EventListener
    @Transactional
    public void BadAdmin(AdminBanEvent adminBanEvent){


        Member member=memberReadService.findByEmail(adminBanEvent.getEmail());

        if(!member.getStatus().equals(MemberStatus.INACTIVE)) {

            log.info("관리자:{} 정지처리",member.getEmail());
            MemberStatusUpdateRequest memberStatusUpdateRequest = MemberStatusUpdateRequest.builder()
                    .status(MemberStatus.INACTIVE)
                    .email(adminBanEvent.getEmail())
                    .build();
            memberService.updateStatus(adminBanEvent.getEmail(), memberStatusUpdateRequest);



        }

    }



}
