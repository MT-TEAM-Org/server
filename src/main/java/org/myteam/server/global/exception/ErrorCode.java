package org.myteam.server.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 500 Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PlayHive Server Error"),
    API_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "API Server Error"),
    IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "File I/O operation failed"),

    // 503 Service Unavailable
    KAFKA_TOPIC_DELETE_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "Failed to delete the Kafka topic."),

    // 400 Bad Request
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "Invalid password"),
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "Not Supported OAuth2 provider"),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter value"),
    EMPTY_COOKIE(HttpStatus.BAD_REQUEST, "Cookie value is empty"),
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "Invalid type provided"),
    MEMBER_NOT_EQUALS(HttpStatus.BAD_REQUEST, "Member Not Equals"),

    // 401 Unauthorized,
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    OAUTH2_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Oauth2 Unauthorized"),
    INVALID_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "Invalid token type"),
    ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Access Token Session has expired. Please log in again."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Refresh Token Session has expired. Please log in again."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token"),
    MISSING_AUTH_HEADER(HttpStatus.UNAUTHORIZED, "No Authorization header or not Bearer type"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Refresh Token"),
    UNAUTHORIZED_EMAIL_ACCOUNT(HttpStatus.UNAUTHORIZED, "Email account verification failed due to an invalid refresh token."),

    // 403 Forbidden
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "Account disabled"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "Account locked"),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "This account has no permission"),
    POST_AUTHOR_MISMATCH(HttpStatus.FORBIDDEN,
            "You are not the author of this post. Only the author can modify or delete it."),
    BAN_USER(HttpStatus.FORBIDDEN, "You are banned from this service."),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found"),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "Board not found"),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "Room not found"),
    BAN_NOT_FOUND(HttpStatus.NOT_FOUND, "Ban not found"),
    NEWS_NOT_FOUND(HttpStatus.NOT_FOUND, "News not found"),
    NEWS_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "New Comment not found"),

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
