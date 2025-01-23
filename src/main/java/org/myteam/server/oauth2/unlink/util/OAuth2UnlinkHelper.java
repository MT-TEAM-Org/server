package org.myteam.server.oauth2.unlink.util;

import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.http.config.RestTemplateConfig;
import org.myteam.server.oauth2.config.OAuth2Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.myteam.server.global.exception.ErrorCode.UNSUPPORTED_OAUTH_PROVIDER;
import static org.myteam.server.oauth2.constant.OAuth2ServiceProvider.*;

@Slf4j
@Component
public class OAuth2UnlinkHelper {

    private static final Map<String, String> LOGOUT_URLS = new HashMap<>();

    static {
        LOGOUT_URLS.put(DISCORD, "https://discord.com/api/oauth2/token/revoke");
        LOGOUT_URLS.put(NAVER, "https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id={CLIENT_ID}&client_secret={CLIENT_SECRET}&access_token={ACCESS_TOKEN}");
        LOGOUT_URLS.put(KAKAO, "https://kapi.kakao.com/v1/user/logout");
        LOGOUT_URLS.put(GOOGLE, "https://oauth2.googleapis.com/revoke?token={ACCESS_TOKEN}");
    }

    @Autowired
    private OAuth2Config oauth2Config;

    @Autowired
    private RestTemplateConfig restTemplateConfig;

    public void revokeToken(String provider, String accessToken) {
        RestTemplate restTemplate = restTemplateConfig.restTemplate();

        String logoutUrl = LOGOUT_URLS.get(provider);
        if (logoutUrl == null) {
            throw new PlayHiveException(UNSUPPORTED_OAUTH_PROVIDER, provider);
        }

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("ACCESS_TOKEN", accessToken);
        uriVariables.put("CLIENT_ID", oauth2Config.getClientId(provider));
        uriVariables.put("CLIENT_SECRET", oauth2Config.getClientSecret(provider));

        // URI 템플릿 치환
        String resolvedLogoutUrl = UriComponentsBuilder.fromUriString(logoutUrl)
                .buildAndExpand(uriVariables)
                .toUriString();

        log.info("Revoking token for provider: {} with URL: {}", provider, logoutUrl);
        log.info("resolvedLogoutUrl: {}", resolvedLogoutUrl);
        log.info("uriVariables: {}", uriVariables);

        try {
            restTemplate.postForObject(resolvedLogoutUrl, null, String.class);
        } catch (Exception e) {
            throw new PlayHiveException(ErrorCode.API_SERVER_ERROR);
        }
    }
}
