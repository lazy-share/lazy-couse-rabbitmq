package com.xxx.integralcenter.repository;

import com.xxx.integralcenter.entity.TMsgConfirmLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *
 * </p>
 *
 * @author laizhiyuan
 * @since 2020/3/16.
 */
@Repository
@Transactional
public interface IMsgConfirmLogRepository extends JpaRepository<TMsgConfirmLogEntity, Long>, JpaSpecificationExecutor<TMsgConfirmLogEntity> {


    TMsgConfirmLogEntity findByMsgIdAndAndAppId(String msgId, String appId);
}
