package org.myteam.server.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String accessToken1d;
    private String accessToken30s;
}