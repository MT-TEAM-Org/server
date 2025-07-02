package org.myteam.server.aop.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CustomLoggingAspect {

    @Around("@annotation(logExecution)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecution logExecution) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        log.info("▶️ [@LogExecution 시작] {}: {}", method, logExecution.value());
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        log.info("✅ [@LogExecution 종료] {} 실행 시간: {}ms", method, (end - start));
        return result;
    }
}
