package org.myteam.server.global.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse<T> {
    private final String timestamp = String.valueOf(LocalDateTime.now());
    private final HttpStatus status;
    private final String message;
    private final T data;

    @Builder
    public ErrorResponse(HttpStatus status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
