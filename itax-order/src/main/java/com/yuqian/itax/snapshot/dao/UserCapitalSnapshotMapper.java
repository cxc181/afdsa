package com.yuqian.itax.snapshot.dao;

import com.yuqian.itax.snapshot.entity.UserCapitalSnapshotEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户资金快照dao
 * 
 * @Date: 2020年10月26日 11:25:02 
 * @author 蒋匿
 */
@Mapper
public interface UserCapitalSnapshotMapper extends BaseMapper<UserCapitalSnapshotEntity> {

    /**
     * 保存资金快照（有资金变动）
     * @param snapshotTime
     * @param oemCode
     */
    void saveUserCapitalSnapshotByChanges(@Param("snapshotTime") String snapshotTime,  @Param("oemCode") String oemCode);

    /**
     * 保存资金快照（没有资金变动）
     * @param snapshotTime
     * @param oemCode
     */
    void saveUserCapitalSnapshotByNotChange(@Param("snapshotTime") String snapshotTime,@Param("oemCode") String oemCode);

    /**
     * 删除资金快照数据
     * @param startDate
     * @param endDate
     * @param oemCode
     */
    void deleteUserCapitalSnapshotSnapshotByDate(@Param("startDate") String startDate,@Param("endDate") String endDate,@Param("oemCode") String oemCode);
	
}

