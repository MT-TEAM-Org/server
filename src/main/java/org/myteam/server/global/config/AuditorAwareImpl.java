package org.myteam.server.global.config;

import org.myteam.server.global.domain.PlayHive;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (null == authentication || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        //사용자 환경에 맞게 로그인한 사용자의 정보를 불러온다.
        UUID publicId = null;

        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            publicId = userDetails.getPublicId();
        } catch (Exception e) {
            return PlayHive.NAME.describeConstable();
        }

        return Optional.of(String.valueOf(publicId));
    }
}
