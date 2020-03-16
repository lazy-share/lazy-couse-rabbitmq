package com.xxx.common;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author laizhiyuan
 * @since 2020/3/15.
 */
public class ResultDto implements Serializable {

    private static final long serialVersionUID = 945244L;


    private String code;
    private String msg;
    private Object data;
    private String traceId;
    private String sign;

    public static ResultDto except(String msg) {
        return new ResultDto()
                .setCode("500")
                .setMsg(msg);
    }

    public static ResultDto faild() {
        return new ResultDto()
                .setCode("400")
                .setMsg("faild");
    }

    public static ResultDto success() {
        return new ResultDto()
                .setCode("200")
                .setMsg("success")
                .setData(null);
    }

    public static ResultDto success(Object data) {
        return new ResultDto()
                .setCode("200")
                .setMsg("success")
                .setData(data);
    }

    public String getCode() {
        return code;
    }

    public ResultDto setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ResultDto setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ResultDto setData(Object data) {
        this.data = data;
        return this;
    }

    public String getTraceId() {
        return traceId;
    }

    public ResultDto setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public ResultDto setSign(String sign) {
        this.sign = sign;
        return this;
    }
}
