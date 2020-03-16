package com.xxx.ordercenter.repository;

import com.xxx.ordercenter.entity.TMsgLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author laizhiyuan
 * @since 2020/3/15.
 */
@Repository
@Transactional
public interface IMsgLogRepository extends JpaRepository<TMsgLogEntity, Long>, JpaSpecificationExecutor<TMsgLogEntity> {


    @Query(
            value = "update TMsgLogEntity t set t.msgStatus = 'SEND', t.lastUpdateTime = ?1 where t.id = ?2"
    )
    @Modifying
    int updateStatusById(LocalDateTime lastUpdateTime, Long msgNumber);

}
