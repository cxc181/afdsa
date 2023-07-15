package com.yuqian.itax.snapshot.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.snapshot.entity.UserCapitalSnapshotEntity;
import com.yuqian.itax.snapshot.dao.UserCapitalSnapshotMapper;

/**
 * 用户资金快照service
 * 
 * @Date: 2020年10月26日 11:25:02 
 * @author 蒋匿
 */
public interface UserCapitalSnapshotService extends IBaseService<UserCapitalSnapshotEntity,UserCapitalSnapshotMapper> {

    /**
     * 保存资金快照
     * @param startDate
     * @param endDate
     * @param oemCode
     */
    void updateOrUserCapitalSnapshot(String startDate, String endDate, String oemCode);


    /**
     * 删除资金快照数据
     * @param startDate
     * @param endDate
     * @param oemCode
     */
    void deleteUserCapitalSnapshotSnapshotByDate(String startDate, String endDate,String oemCode);
}

