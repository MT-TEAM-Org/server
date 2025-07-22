package org.myteam.server.aop.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.myteam.server.board.dto.request.BoardServiceRequest;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    @Around("execution(* org.myteam.server..*Service.*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        // ✅ 요청 DTO 필드 출력
        for (Object arg : args) {
            /**
             * 예시
             * if (arg instanceof MemberRequestDto dto) {
             *     log.info("📨 [요청 DTO] email: {}, nickname: {}", dto.getEmail(), dto.getNickname());
             * }
             */
            if (arg instanceof MemberSaveRequest dto) {
                log.info("📨 [회원가입 요청 DTO] email: {}, nickname: {}", dto.getEmail(), dto.getNickname());
            } else if (arg instanceof BoardServiceRequest dto) {
                log.info("📨 [게시판 목록 조회 요청 DTO] boardType: {}, categoryType: {}, searchType: {}, keyword: {}",
                        dto.getBoardType(), dto.getCategoryType(), dto.getSearchType(), dto.getSearch());
            }
        }

        long start = System.currentTimeMillis();

        Object result = null;
        try {
            log.info("▶️ [서비스 호출] {}", methodName);
            result = joinPoint.proceed(); // 실제 서비스 메서드 실행
            return result;
        } finally {
            long end = System.currentTimeMillis();
            log.info("✅ [서비스 완료] {} ({}ms)", methodName, end - start);

            // ✅ 리턴값 로깅
            if (result != null) {
                log.info("🔁 [리턴값] {}", result);
            }
        }
    }
}
