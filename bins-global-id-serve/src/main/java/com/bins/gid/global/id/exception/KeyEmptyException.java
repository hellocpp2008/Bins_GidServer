package com.bins.gid.global.id.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zoo
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Key is none")
public class KeyEmptyException extends RuntimeException {

    public KeyEmptyException(String msg) {
        super(msg);
    }
}
