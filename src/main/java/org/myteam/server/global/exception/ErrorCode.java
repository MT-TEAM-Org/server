package org.myteam.server.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 500 Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PlayHive Server Error"),
    API_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "API Server Error"),

    // 503 Service Unavailable
    KAFKA_TOPIC_DELETE_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "Failed to delete the Kafka topic."),

    // 400 Bad Request
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "Invalid password"),
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "Not Supported OAuth2 provider"),

    // 401 Unauthorized,
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Access Token Session has expired. Please log in again."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Refresh Token Session has expired. Please log in again."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token"),
    MISSING_AUTH_HEADER(HttpStatus.UNAUTHORIZED, "No Authorization header or not Bearer type"),

    // 403 Forbidden
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "Account disabled"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "Account locked"),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "This account has no permission"),
    BAN_USER(HttpStatus.FORBIDDEN, "You are banned from this service."),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "Room not found"),
    BAN_NOT_FOUND(HttpStatus.NOT_FOUND, "Ban not found"),

    // 409 Conflict,
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists"),
    BAN_ALREADY_EXISTS(HttpStatus.CONFLICT, "This user already exists");

    private final HttpStatus status;
    private final String msg;

    ErrorCode(HttpStatus status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
