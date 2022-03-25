package com.bins.id.global.api;

import java.io.Serializable;
import lombok.Data;

@Data
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private int code;
    private String msg;
    private T result;

    public ResultVO() {
    }

    /**
     * 构造器1   默认成功
     * @param result
     */
    public ResultVO(T result) {
        this(ResultCodeEnum.SUCCESS, result);
    }

    /**
     * 构造器2  不推荐使用这个，resultCode值应该从枚举里面取
     * @param resultCodeEnum
     * @param msg
     * @param result
     */
    @Deprecated
    public ResultVO(Integer resultCodeEnum, String msg, T result) {
        this.result = result;
        this.code = resultCodeEnum.intValue();
        this.msg = msg;
    }

    /**
     * 构造器3  返回的msg使用默认的
     * @param resultCodeEnum
     * @param result
     */
    public ResultVO(ResultCodeEnum resultCodeEnum, T result) {
        this.result = result;
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMsg();
    }

    /**
     * 构造器4   每个参数值都传
     * @param resultCodeEnum
     * @param msg
     * @param result
     */
    public ResultVO(ResultCodeEnum resultCodeEnum, String msg, T result) {
        this.result = result;
        this.code = resultCodeEnum.getCode();
        this.msg = msg;
    }

    /**
     * 构造器5   不传入T result)
     * @param resultCodeEnum
     * @param msg
     */
    public ResultVO(ResultCodeEnum resultCodeEnum, String msg) {
        this.code = resultCodeEnum.getCode();
        this.msg = msg;
    }

    /**
     *
     * @param result
     * @param <T>
     * @return
     */
    public static <T> ResultVO<T> createSuccess(T result) {
        return new ResultVO(ResultCodeEnum.SUCCESS, result);
    }

    /**
     *
     * @param resultCodeEnum
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> ResultVO<T> createError(ResultCodeEnum resultCodeEnum, String msg) {
        return new ResultVO(resultCodeEnum, msg);
    }

}

