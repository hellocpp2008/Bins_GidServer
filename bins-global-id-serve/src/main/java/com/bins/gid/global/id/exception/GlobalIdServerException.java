package com.bins.gid.global.id.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Desc:
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class GlobalIdServerException extends RuntimeException {

    public GlobalIdServerException(String msg) {
        super(msg);
    }
}
