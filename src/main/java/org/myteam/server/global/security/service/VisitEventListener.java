package org.myteam.server.global.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.admin.entity.UserAccessLog;
import org.myteam.server.admin.service.UserAccessLogService;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.dto.UserLoginEvent;
import org.myteam.server.global.util.redis.service.RedisService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberActivity;
import org.myteam.server.member.repository.MemberActivityRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitEventListener {

    private final MemberActivityRepository memberActivityRepository;
    private final UserAccessLogService userAccessLogService;
    private final RedisService redisService;
    private final RedissonClient redissonClient;

    @Transactional
    @EventListener
    public void handleUserLoginEvent(UserLoginEvent event) {
        UUID publicId = event.getPublicId();

        log.info("handleUserLoginEvent > 방문 횟수 증가: {}", publicId);

        MemberActivity activity = memberActivityRepository.findByMemberPublicId(publicId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.USER_NOT_FOUND));

        activity.increaseVisitCount();


        String rockKey="userLogTry"+publicId.toString();
        //첫날에햿던 질문에대한 답이 이제야 이해가되내요.
        RLock lock=redissonClient.getLock(rockKey);


        try {
            boolean lockState=lock.tryLock(3L,2L, TimeUnit.SECONDS);


            if(lockState) {

                long ttl = redisService.getTimeToLive("userLog", publicId.toString());
                if (0 >= ttl) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime left = now.plusDays(1L).with(LocalTime.MIDNIGHT);

                    Long second = Duration.between(left, now).getSeconds();
                    redisService.putRedisKeyWithTimeOut("userLog", publicId.toString(), TimeUnit.SECONDS, second);
                    UserAccessLog userAccessLog = UserAccessLog.builder()
                            .id(publicId)
                            .build();
                    userAccessLogService.makeLog(publicId, now);
                }
            }

        }
        catch (Exception e){

            log.info("로그인 기록 남기기에 실패하였습니다:{}-{}",LocalDateTime.now(),publicId.toString());
        }



    }
}