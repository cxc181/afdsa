package com.yuqian.itax.snapshot.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.snapshot.entity.EmployeesSnapshotEntity;
import com.yuqian.itax.snapshot.dao.EmployeesSnapshotMapper;

/**
 * 员工快照service
 * 
 * @Date: 2020年10月26日 11:24:55 
 * @author 蒋匿
 */
public interface EmployeesSnapshotService extends IBaseService<EmployeesSnapshotEntity,EmployeesSnapshotMapper> {

    /**
     * 新增或修复员工用户快照
     */
    void updateOrEmployeeSnapshot(String startDate,String endDate,Long userId,String oemCode);
    /**
     * 删除
     */
    void deleteEmployeeSnapshotByDate(String startDate,String endDate,Long userId,String oemCode);
}

