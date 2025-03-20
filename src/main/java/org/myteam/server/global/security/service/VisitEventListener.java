package org.myteam.server.global.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.dto.UserLoginEvent;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberActivity;
import org.myteam.server.member.repository.MemberActivityRepository;
import org.myteam.server.member.repository.MemberRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitEventListener {

    private final MemberActivityRepository memberActivityRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserLoginEvent(UserLoginEvent event) {
        UUID publicId = event.getPublicId();

        log.info("handleUserLoginEvent > 방문 횟수 증가: {}", publicId);

        MemberActivity activity = memberActivityRepository.findByMemberPublicId(publicId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.USER_NOT_FOUND));

        activity.increaseVisitCount();
    }
}