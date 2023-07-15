package com.yuqian.itax.snapshot.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.snapshot.dao.UserCapitalSnapshotMapper;
import com.yuqian.itax.snapshot.entity.UserCapitalSnapshotEntity;
import com.yuqian.itax.snapshot.service.UserCapitalSnapshotService;
import com.yuqian.itax.util.util.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service("userCapitalSnapshotService")
public class UserCapitalSnapshotServiceImpl extends BaseServiceImpl<UserCapitalSnapshotEntity,UserCapitalSnapshotMapper> implements UserCapitalSnapshotService {

    /**
     * 保存资金快照
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrUserCapitalSnapshot(String startDate,String endDate,String oemCode){
        Date startD = DateUtil.parseDate(startDate+" 00:00:00","yyyy-MM-dd HH:mm:ss");
        Date endD = DateUtil.parseDate(endDate +" 23:59:59","yyyy-MM-dd HH:mm:ss");
        Date yesterDay = DateUtil.parseDate(DateUtil.stampToDate(DateUtil.getYesterdayEndTime()),"yyyy-MM-dd HH:mm:ss");
        String snapshotTime = null;
        while(DateUtil.diffDate(endD,startD) >=0 && DateUtil.diffDate(yesterDay,endD) >=0) {
            snapshotTime = DateUtil.format(startD,"yyyy-MM-dd HH:mm:ss");
            //保存有收益的员工快照
            this.mapper.saveUserCapitalSnapshotByChanges(snapshotTime, oemCode);
            //保存没有收益的员工快照
            this.mapper.saveUserCapitalSnapshotByNotChange(snapshotTime, oemCode);
            startD = DateUtil.addDate(startD,1);
        }
    }

    /**
     * 删除资金快照
     */
    public void deleteUserCapitalSnapshotSnapshotByDate(String startDate,String endDate,String oemCode){
        Date endD = DateUtil.parseDate(endDate +" 23:59:59","yyyy-MM-dd HH:mm:ss");
        Date yesterDay = DateUtil.parseDate(DateUtil.stampToDate(DateUtil.getYesterdayEndTime()),"yyyy-MM-dd HH:mm:ss");
        if(DateUtil.diffDate(yesterDay,endD) < 0 ){
            endDate = DateUtil.format(yesterDay,"yyyy-MM-dd HH:mm:ss");
        }
        this.mapper.deleteUserCapitalSnapshotSnapshotByDate(startDate,endDate,oemCode);
    }
}

