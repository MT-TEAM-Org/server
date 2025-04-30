package org.myteam.server.global.web.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResponseDto<T> {
    private final String status; // SUCCESS 성공, FAIL 실패
    private final String msg;
    private final T data; // <T> 이거 해주어야 되네...
}
