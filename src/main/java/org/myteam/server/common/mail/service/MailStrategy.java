package org.myteam.server.common.mail.service;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;

public interface MailStrategy {
    void send (String email);

    default boolean verify(String email, String code) {
        throw new PlayHiveException(ErrorCode.NOT_SUPPORT_TYPE);
    }
}
