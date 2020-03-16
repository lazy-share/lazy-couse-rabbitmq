package com.xxx.ordercenter.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author laizhiyuan
 * @since 2020/3/15.
 */
@Entity
@Table(name = "t_msg_log")
public class TMsgLogEntity implements Serializable {

    private static final long serialVersionUID = 35151514L;

    public static final String SEND = "SEND";
    public static final String UN_SEND = "UN_SEND";

    @Id
    @Column(name = "id")
    //主键自增
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "biz_id")
    private Long bizId;
    @Column(name = "biz_type")
    private String bizType;
    @Column(name = "biz_val")
    private String bizVal;
    @Column(name = "biz_user_id")
    private Long bizUserId;
    @Column(name = "msg_status")
    private String msgStatus;
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    @Column(name = "create_time")
    @JsonIgnore
    private LocalDateTime createTime;
    @Column(name = "last_update_time")
    @JsonIgnore
    private LocalDateTime lastUpdateTime;
    @Transient
    private String appId = "ordercenter";

    public String getAppId() {
        return appId;
    }

    public TMsgLogEntity setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public Long getId() {
        return id;
    }

    public TMsgLogEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getBizId() {
        return bizId;
    }

    public TMsgLogEntity setBizId(Long bizId) {
        this.bizId = bizId;
        return this;
    }

    public String getBizType() {
        return bizType;
    }

    public TMsgLogEntity setBizType(String bizType) {
        this.bizType = bizType;
        return this;
    }

    public String getBizVal() {
        return bizVal;
    }

    public TMsgLogEntity setBizVal(String bizVal) {
        this.bizVal = bizVal;
        return this;
    }

    public Long getBizUserId() {
        return bizUserId;
    }

    public TMsgLogEntity setBizUserId(Long bizUserId) {
        this.bizUserId = bizUserId;
        return this;
    }

    public String getMsgStatus() {
        return msgStatus;
    }

    public TMsgLogEntity setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
        return this;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public TMsgLogEntity setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public TMsgLogEntity setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public TMsgLogEntity setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }
}
