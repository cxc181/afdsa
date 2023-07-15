package com.yuqian.itax.snapshot.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.snapshot.entity.UserSnapshotEntity;
import com.yuqian.itax.snapshot.dao.UserSnapshotMapper;

/**
 * 系统用户快照service
 * 
 * @Date: 2020年10月26日 11:25:11 
 * @author 蒋匿
 */
public interface UserSnapshotService extends IBaseService<UserSnapshotEntity,UserSnapshotMapper> {

    /**
     * 新增或修复系统用户快照
     */
    void updateOrUserSnapshot(String startDate,String endDate,String oemCode);
    /**
     * 删除用户快照
     */
    void deleteUserSnapshotByDate(String startDate,String endDate,String oemCode);
}

