package org.myteam.server.aop.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    @Around("execution(* org.myteam.server..*Controller.*(..))")
    public Object logApiRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        long start = System.currentTimeMillis();
        try {
            log.info("▶️ API 호출: {} args = {}", methodName, Arrays.toString(args));
            Object result = joinPoint.proceed(); // 실제 메서드 실행
            log.info("✅ API 결과: {} result = {}", methodName, result);
            return result;
        } catch (Throwable e) {
            log.error("❌ API 에러: {} message = {}", methodName, e.getMessage(), e);
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            log.info("⏱ API 실행 시간: {} ms", (end - start));
        }
    }
}
