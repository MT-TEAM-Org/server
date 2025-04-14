package org.myteam.server.global.exception;

import java.util.Map;

import lombok.Getter;

@Getter
public class PlayHiveJwtException extends RuntimeException {

    private final ErrorCode errorCode;
    private Map<String, String> errorMap;

    /**
     * @param errorCode ErrorCode에 정의된 메시지 반환
     */
    public PlayHiveJwtException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    /**
     * @param errorCode ErrorCode 에 정의된 메시지 반환
     * @param errorMap 필드에 대한 에러를 담은 Map
     */
    public PlayHiveJwtException(ErrorCode errorCode, Map<String, String> errorMap) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
        this.errorMap = errorMap;
    }

    /**
     * @param errorCode ErrorCode에 정의된 메시지 반환
     * @param message 정의되지 않은 예외 처리
     */
    public PlayHiveJwtException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PlayHiveJwtException(ErrorCode errorCode, Object... args) {
        super(errorCode.getFormattedMessage(args));
        this.errorCode = errorCode;
    }
}
