package org.myteam.server.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class CommonCountAspect {

    private final HttpServletRequest request;
    private final RedisCountService redisCountService;

    private static final List<String> BOT_AGENTS = List.of(
            "Slackbot",
            "facebookexternalhit",
            "Twitterbot",
            "Discordbot",
            "LinkedInBot",
            "WhatsApp",
            "TelegramBot",
            "Googlebot",
            "bingbot",
            "Applebot",
            "Yeti",
            "Daumoa",
            "KAKAOTALK"
    );

    @Around("@annotation(countView)")
    public Object handleCommonCount(ProceedingJoinPoint joinPoint, CountView countView) throws Throwable {
        String userAgent = request.getHeader("User-Agent");
        boolean isBot = userAgent != null && BOT_AGENTS.stream()
                .anyMatch(bot -> userAgent.toLowerCase().contains(bot.toLowerCase()));

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        String idParam = countView.idParam();
        Long contentId = null;

        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(idParam) && args[i] instanceof Long) {
                contentId = (Long) args[i];
                break;
            }
        }

        if (contentId != null) {
            redisCountService.getCommonCount(
                    isBot ? ServiceType.CHECK : ServiceType.VIEW,
                    countView.domain(),
                    contentId,
                    null
            );
        }

        return joinPoint.proceed();
    }
}
