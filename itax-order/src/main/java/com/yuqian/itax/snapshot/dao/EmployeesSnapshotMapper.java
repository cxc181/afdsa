package com.yuqian.itax.snapshot.dao;

import com.yuqian.itax.snapshot.entity.EmployeesSnapshotEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 员工快照dao
 * 
 * @Date: 2020年10月26日 11:24:55 
 * @author 蒋匿
 */
@Mapper
public interface EmployeesSnapshotMapper extends BaseMapper<EmployeesSnapshotEntity> {

    /**
     * 保存员工快照（有收益）
     * @param snapshotTime
     * @param userId
     */
    void saveEmployeeSnapshotByProfits(@Param("snapshotTime") String snapshotTime,@Param("userId") Long userId,@Param("oemCode") String oemCode);

    /**
     * 保存员工快照（没有收益）
     * @param snapshotTime
     * @param userId
     */
    void saveEmployeeSnapshotByNotChange(@Param("snapshotTime") String snapshotTime,@Param("userId") Long userId,@Param("oemCode") String oemCode);

    /**
     * 根据条件删除员工快照数据
     * @param startDate
     * @param endDate
     * @param userId
     */
    void deleteEmployeeSnapshotByDate(@Param("startDate") String startDate,@Param("endDate") String endDate,@Param("userId") Long userId,@Param("oemCode") String oemCode);
	
}

