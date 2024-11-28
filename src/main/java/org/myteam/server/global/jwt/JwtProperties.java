package org.myteam.server.global.jwt;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties("jwt")
public class JwtProperties {
    private final String issuer;
    private final String secretKey;

    @ConstructorBinding
    public JwtProperties(String issuer, String secretKey) {
        this.issuer = issuer;
        this.secretKey = secretKey;
    }
}