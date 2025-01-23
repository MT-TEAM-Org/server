package org.myteam.server.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Tokens {
    private final String accessToken;
    private final String refreshToken;
}
