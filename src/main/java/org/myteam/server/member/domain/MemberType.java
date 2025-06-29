package org.myteam.server.member.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.oauth2.constant.OAuth2ServiceProvider;

import static org.myteam.server.global.exception.ErrorCode.UNSUPPORTED_OAUTH_PROVIDER;

@AllArgsConstructor
@Getter
public enum MemberType {
    LOCAL("LOCAL"),
    NAVER(OAuth2ServiceProvider.NAVER),
    GOOGLE(OAuth2ServiceProvider.GOOGLE),
    DISCORD(OAuth2ServiceProvider.DISCORD),
    KAKAO(OAuth2ServiceProvider.KAKAO);

    private final String value;


    // OAuth2ServiceProvider의 값과 일치하는 MemberType을 반환하는 메서드
    public static MemberType fromOAuth2Provider(String provider) {
        for (MemberType type : values()) {
            if (type.getValue().equals(provider)) {
                return type;
            }
        }
        throw new PlayHiveException(UNSUPPORTED_OAUTH_PROVIDER, provider);
    }
}
