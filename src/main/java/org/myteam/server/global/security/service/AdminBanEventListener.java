package org.myteam.server.global.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.security.dto.AdminBanEvent;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.MemberStatusUpdateRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.MemberService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.myteam.server.member.dto.MemberStatusUpdateRequest.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminBanEventListener {

    private final MemberService memberService;
    private final MemberReadService memberReadService;


    @EventListener
    @Transactional
    public void BadAdmin(AdminBanEvent adminBanEvent){
        Member member=memberReadService.
                findByEmailAndType(adminBanEvent.getEmail(), MemberType.LOCAL);

        if(!member.getStatus().equals(MemberStatus.INACTIVE)) {

            log.info("관리자:{} 정지처리",member.getEmail());

           MemberStatusUpdateRequest memberStatusUpdateRequest=
                   memberStatusUpdateRequestBuilder(member.getEmail(),MemberStatus.INACTIVE);

            memberService.updateStatus(adminBanEvent.getEmail(), memberStatusUpdateRequest);

        }
    }



}
