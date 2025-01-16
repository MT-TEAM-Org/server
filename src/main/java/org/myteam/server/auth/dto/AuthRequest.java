package org.myteam.server.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthRequest {
    private String email;
    private String password;
}
