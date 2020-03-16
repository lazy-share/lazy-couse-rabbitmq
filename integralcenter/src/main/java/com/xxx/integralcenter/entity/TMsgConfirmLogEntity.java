package com.xxx.integralcenter.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author laizhiyuan
 * @since 2020/3/16.
 */
@Entity
@Table(name = "t_msg_confirm_log")
public class TMsgConfirmLogEntity implements Serializable {

    private static final long serialVersionUID = 35151566614L;


    @Id
    @Column(name = "id")
    //主键自增
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "msg_id")
    private String msgId;
    @Column(name = "app_id")
    private String appId;
    @Column(name = "create_time")
    private LocalDateTime createTime;
    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;

    public Long getId() {
        return id;
    }

    public TMsgConfirmLogEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public String getMsgId() {
        return msgId;
    }

    public TMsgConfirmLogEntity setMsgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public TMsgConfirmLogEntity setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public TMsgConfirmLogEntity setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public TMsgConfirmLogEntity setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }
}
