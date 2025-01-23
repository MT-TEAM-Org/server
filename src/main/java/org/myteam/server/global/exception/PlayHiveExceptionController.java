package org.myteam.server.global.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PlayHiveExceptionController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = PlayHiveException.class)
    public ResponseEntity<?> serviceException(PlayHiveException e) {
        return ResponseEntity
                   .status(e.getErrorCode().getStatus())
                   .body(
                       new ErrorResponse(
                           e.getErrorCode().getStatus(),
                           e.getMessage(),
                           e.getErrorMap()
                       )
                   );
    }
}
