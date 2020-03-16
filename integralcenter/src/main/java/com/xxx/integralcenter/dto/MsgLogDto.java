package com.xxx.integralcenter.dto;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author laizhiyuan
 * @since 2020/3/15.
 */
public class MsgLogDto implements Serializable {

    private static final long serialVersionUID = 35151514L;


    private Long id;
    private Long bizId;
    private String bizType;
    private String bizVal;
    private Long bizUserId;
    private String msgStatus;
    private Integer retryCount = 0;
    private String appId;

    public String getAppId() {
        return appId;
    }

    public MsgLogDto setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public Long getId() {
        return id;
    }

    public MsgLogDto setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getBizId() {
        return bizId;
    }

    public MsgLogDto setBizId(Long bizId) {
        this.bizId = bizId;
        return this;
    }

    public String getBizType() {
        return bizType;
    }

    public MsgLogDto setBizType(String bizType) {
        this.bizType = bizType;
        return this;
    }

    public String getBizVal() {
        return bizVal;
    }

    public MsgLogDto setBizVal(String bizVal) {
        this.bizVal = bizVal;
        return this;
    }

    public Long getBizUserId() {
        return bizUserId;
    }

    public MsgLogDto setBizUserId(Long bizUserId) {
        this.bizUserId = bizUserId;
        return this;
    }

    public String getMsgStatus() {
        return msgStatus;
    }

    public MsgLogDto setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
        return this;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public MsgLogDto setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
        return this;
    }
}
