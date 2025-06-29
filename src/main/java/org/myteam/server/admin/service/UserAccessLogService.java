package org.myteam.server.admin.service;


import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.entity.UserAccessLog;
import org.myteam.server.admin.repository.UserAccessLogRepo;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.service.RedisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserAccessLogService {


    private final UserAccessLogRepo userAccessLogRepo;

    public void makeLog(UUID id, LocalDateTime now){

        UserAccessLog userAccessLog=UserAccessLog.builder()
                .id(id)
                .build();

        userAccessLogRepo.save(userAccessLog);

    }
}
