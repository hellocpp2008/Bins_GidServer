package com.bins.gid.global.id.exception;

/**
 * Desc: 本机时间 VS zookeeper上时间 => 出现异常
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
public class CheckLastTimeException extends RuntimeException {

    public CheckLastTimeException(String msg) {
        super(msg);
    }

}
