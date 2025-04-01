package org.myteam.server.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 500 Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PlayHive Server Error"),
    API_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "API Server Error"),
    IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "File I/O operation failed"),
    JSON_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JSON Parsing error"),
    ENCRYPTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during encryption."),
    DECRYPTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during decryption."),
    NOT_SUPPORT_TYPE(HttpStatus.INTERNAL_SERVER_ERROR, "This email type is not supported"),
    SEND_EMAIL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Occur error during send email"),
    CREATE_EMAIL_ACCOUNT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Occur error during create email"),
    MISSING_ENVIRONMENT_VARIABLE(HttpStatus.INTERNAL_SERVER_ERROR, "Environment variable not found"),

    // 503 Service Unavailable
    KAFKA_TOPIC_DELETE_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "Failed to delete the Kafka topic."),

    // 400 Bad Request
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "Invalid email request"),
    INQUIRY_EMAIL_EMPTY(HttpStatus.BAD_REQUEST, "비로그인 문의하기는 이메일이 필수입니다."),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "Invalid password"),
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "Not Supported OAuth2 provider"),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter value"),
    EMPTY_COOKIE(HttpStatus.BAD_REQUEST, "Cookie value is empty"),
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "Invalid type provided"),
    INVALID_MIME_TYPE(HttpStatus.BAD_REQUEST, "Invalid mime type provided"),
    MEMBER_NOT_EQUALS(HttpStatus.BAD_REQUEST, "Member Not Equals"),
    NO_MEMBER_RECOMMEND_RECORD(HttpStatus.BAD_REQUEST, "No Member Recommend Record"),
    ALREADY_MEMBER_RECOMMEND_NEWS(HttpStatus.BAD_REQUEST, "Member Already Recommend News"),
    ALREADY_MEMBER_RECOMMEND_NEWS_COMMENT(HttpStatus.BAD_REQUEST, "Member Already Recommend News Comment"),
    ALREADY_MEMBER_RECOMMEND_NEWS_REPLY(HttpStatus.BAD_REQUEST, "Member Already Recommend News Reply"),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "Phone number is invalid"),
    INVALID_GENDER_TYPE(HttpStatus.BAD_REQUEST, "Gender Type allow M or F"),
    INVALID_BIRTH_DATE(HttpStatus.BAD_REQUEST, "BIRTHDATE length must be 6"),
    INVALID_BIRTH_MONTH(HttpStatus.BAD_REQUEST, "Month is between 1 and 12"),
    INVALID_BIRTH_DAY(HttpStatus.BAD_REQUEST, "Day is not allowed"),
    INVALID_IMPROVEMENT_STATUS(HttpStatus.BAD_REQUEST, "Improvement status is invalid"),
    INVALID_REPORT_MEMBER(HttpStatus.BAD_REQUEST, "Reported user and report user is same"),
    INVALID_REPORT_TYPE(HttpStatus.BAD_REQUEST, "Not Invalid report Type"),
    INVALID_REPORT_CONTENT_OWNER(HttpStatus.BAD_REQUEST, "This content is not author"),
    LIMIT_COMMENT_DEPTH(HttpStatus.BAD_REQUEST, "더이상 대댓글을 작성할 수 없습니다."),
    NOT_SUPPORT_COMMENT_TYPE(HttpStatus.BAD_REQUEST, "This comment type is not supported"),
    COMMENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "부모 댓글의 타입과 요청된 타입이 일치하지 않음"),

    // 401 Unauthorized,
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    INVALID_USER(HttpStatus.UNAUTHORIZED, "This User already deleted"),
    OAUTH2_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Oauth2 Unauthorized"),
    INVALID_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "Invalid token type"),
    ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Access Token Session has expired. Please log in again."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "Refresh Token Session has expired. Please log in again."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token"),
    MISSING_AUTH_HEADER(HttpStatus.UNAUTHORIZED, "No Authorization header or not Bearer type"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Refresh Token"),
    UNAUTHORIZED_EMAIL_ACCOUNT(HttpStatus.UNAUTHORIZED,
            "Email account verification failed due to an invalid refresh token."),

    // 403 Forbidden
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "Account disabled"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "Account locked"),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "This account has no permission"),
    POST_AUTHOR_MISMATCH(HttpStatus.FORBIDDEN,
            "You are not the author of this post. Only the author can modify or delete it."),
    BAN_USER(HttpStatus.FORBIDDEN, "You are banned from this service."),
    REPORT_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "5분 내 최대 3번까지 신고할 수 있습니다. 다시 시도 가능: {ttl}초 후"),

    // 404 Not Found
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "부모 댓글 없음"),
    NOT_FOUND_CERTIFICATION_CODE(HttpStatus.NOT_FOUND, "인증코드 없음"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found"),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "Room not found"),
    BAN_NOT_FOUND(HttpStatus.NOT_FOUND, "Ban not found"),

    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "Inquiry not found"),
    INQUIRY_RECOMMEND_NOT_FOUND(HttpStatus.NOT_FOUND, "Inquiry Recommend not found"),
    INQUIRY_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Inquiry Comment not found"),
    INQUIRY_ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "Inquiry answer not found"),
    INQUIRY_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "Inquiry Reply not found"),

    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "Notice Not Found"),
    NOTICE_COUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Notice Count Not Found"),
    NOTICE_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Notice Comment Count Not Found"),
    NOTICE_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "Notice Comment Count Not Found"),
    IMPROVEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Improvement Not Found"),
    IMPROVEMENT_COUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Improvement Count Not Found"),
    IMPROVEMENT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Improvement Comment Count Not Found"),
    IMPROVEMENT_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "Improvement Comment Count Not Found"),

    NEWS_NOT_FOUND(HttpStatus.NOT_FOUND, "News not found"),
    NEWS_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "News Comment not found"),
    NEWS_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "News Reply not found"),
    NEWS_COUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "News Count not found"),
    NEWS_COMMENT_RECOMMEND_NOT_FOUND(HttpStatus.NOT_FOUND, "News Comment Recommend not found"),

    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "Board not found"),
    BOARD_RECOMMEND_NOT_FOUND(HttpStatus.NOT_FOUND, "Board Recommend not found"),
    PHONE_NUMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "No matching number exists"),
    BOARD_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Board Comment not found"),
    BOARD_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "Board Reply not found"),
    BOARD_COMMENT_RECOMMEND_NOT_FOUND(HttpStatus.NOT_FOUND, "Board Comment Recommend not found"),
    BOARD_REPLY_RECOMMEND_NOT_FOUND(HttpStatus.NOT_FOUND, "Board Reply Recommend not found"),

    MATCH_NOT_FOUNT(HttpStatus.NOT_FOUND, "Match not found"),

    MATCH_PREDICTION_NOT_FOUNT(HttpStatus.NOT_FOUND, "Match Prediction not found"),


    MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "Match not found"),
    MATCH_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Match Comment not found"),
    MATCH_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "Match Reply not found"),
    MATCH_COUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Match Count not found"),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "Report not found"),
    REPORTED_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Report Content not found"),


    // 409 Conflict,
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists"),
    BAN_ALREADY_EXISTS(HttpStatus.CONFLICT, "This user already exists"),
    INQUIRY_ANSWER_ALREADY_EXISTS(HttpStatus.CONFLICT, "This inquiry already exists"),
    ALREADY_MEMBER_RECOMMEND_NOTICE(HttpStatus.CONFLICT, "Member Already Recommend NOTICE"),
    ALREADY_MEMBER_RECOMMEND_BOARD(HttpStatus.CONFLICT, "Member Already Recommend Board"),
    ALREADY_MEMBER_RECOMMEND_REPLY(HttpStatus.CONFLICT, "Member Already Recommend Reply"),
    ALREADY_MEMBER_RECOMMEND_COMMENT(HttpStatus.CONFLICT, "Member Already Recommend Comment");

    private final HttpStatus status;
    private final String msg;

    ErrorCode(HttpStatus status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    /**
     * 동적인 메시지 생성 (예: TTL 값을 포함)
     */
    public String getFormattedMessage(Object... args) {
        return String.format(this.msg.replace("{ttl}", "%s"), args);
    }
}
