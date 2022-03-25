package com.bins.id.global.api;

/**
 Desc:
 <p>
 * Created by jxyi on 2020/1/17.
 */
public enum ResultCodeEnum {
    SUCCESS(0, "success"),
    ERROR(1, "error"),

    NO_PARAMS(10001, "入参不可以为空"),
    REQUEST_TOO_FREQUENTLY(10002, "请求过于频繁"),

    LOGIN_INDEX_PAGE(20000, "LOGIN_INDEX_PAGE"),
    TOKEN_ERROR(20001, "TOKEN ERROR"),
    ;

    private int code;
    private String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
