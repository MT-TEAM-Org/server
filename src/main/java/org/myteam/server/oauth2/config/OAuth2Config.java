package org.myteam.server.oauth2.config;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.oauth2.properties.OAuth2ClientProperties;
import org.springframework.context.annotation.Configuration;

import static org.myteam.server.global.exception.ErrorCode.UNSUPPORTED_OAUTH_PROVIDER;
import static org.myteam.server.oauth2.constant.OAuth2ServiceProvider.*;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config {

    private final OAuth2ClientProperties properties;

    public String getClientId(String provider) {
        switch (provider.toLowerCase()) {
            case DISCORD:
                return properties.getDiscord().getClientId();
            case NAVER:
                return properties.getNaver().getClientId();
            case KAKAO:
                return properties.getKakao().getClientId();
            case GOOGLE:
                return properties.getGoogle().getClientId();
            default:
                throw new PlayHiveException(UNSUPPORTED_OAUTH_PROVIDER, provider);
        }
    }

    public String getClientSecret(String provider) {
        switch (provider.toLowerCase()) {
            case DISCORD:
                return properties.getDiscord().getClientSecret();
            case NAVER:
                return properties.getNaver().getClientSecret();
            case KAKAO:
                return properties.getKakao().getClientSecret();
            case GOOGLE:
                return properties.getGoogle().getClientSecret();
            default:
                throw new PlayHiveException(UNSUPPORTED_OAUTH_PROVIDER, provider);
        }
    }
}
