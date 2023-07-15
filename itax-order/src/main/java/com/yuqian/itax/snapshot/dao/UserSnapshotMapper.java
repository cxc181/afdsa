package com.yuqian.itax.snapshot.dao;

import com.yuqian.itax.snapshot.entity.UserSnapshotEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户快照dao
 * 
 * @Date: 2020年10月26日 11:25:11 
 * @author 蒋匿
 */
@Mapper
public interface UserSnapshotMapper extends BaseMapper<UserSnapshotEntity> {

    /**
     * 新增或修复系统用户快照
     */
    void updateOrInsertUserSnapshot(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("oemCode") String oemCode);
    /**
     * 删除快照
     */
    void deleteUserSnapshotByDate(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("oemCode") String oemCode);
}

