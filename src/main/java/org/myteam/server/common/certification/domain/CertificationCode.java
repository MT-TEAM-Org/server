package org.myteam.server.common.certification.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CertificationCode {
    private final String code;
    private final LocalDateTime expirationTime;
}
