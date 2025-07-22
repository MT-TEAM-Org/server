package org.myteam.server.common.certification.mail.core;

import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;

import java.util.concurrent.CompletableFuture;

public interface MailStrategy {
    EmailType getType();
    CompletableFuture<Void> send (String email);

    default boolean verify(String email, String code) {
        throw new PlayHiveException(ErrorCode.NOT_SUPPORT_TYPE);
    }
}
