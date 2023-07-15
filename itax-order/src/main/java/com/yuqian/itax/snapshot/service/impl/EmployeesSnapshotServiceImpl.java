package com.yuqian.itax.snapshot.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.snapshot.dao.EmployeesSnapshotMapper;
import com.yuqian.itax.snapshot.entity.EmployeesSnapshotEntity;
import com.yuqian.itax.snapshot.service.EmployeesSnapshotService;
import com.yuqian.itax.util.util.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;


@Service("employeesSnapshotService")
public class EmployeesSnapshotServiceImpl extends BaseServiceImpl<EmployeesSnapshotEntity,EmployeesSnapshotMapper> implements EmployeesSnapshotService {


    /**
     * 新增或修复员工用户快照
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrEmployeeSnapshot(String startDate,String endDate,Long userId,String oemCode){
        Date startD = DateUtil.parseDate(startDate+" 00:00:00","yyyy-MM-dd HH:mm:ss");
        Date endD = DateUtil.parseDate(endDate +" 23:59:59","yyyy-MM-dd HH:mm:ss");
        Date yesterDay = DateUtil.parseDate(DateUtil.stampToDate(DateUtil.getYesterdayEndTime()),"yyyy-MM-dd HH:mm:ss");
        String snapshotTime = null;
        while(DateUtil.diffDate(endD,startD) >=0 && DateUtil.diffDate(yesterDay,endD) >=0) {
            snapshotTime = DateUtil.format(startD,"yyyy-MM-dd HH:mm:ss");
            //保存有收益的员工快照
            this.mapper.saveEmployeeSnapshotByProfits(snapshotTime, userId, oemCode);
            //保存没有收益的员工快照
            this.mapper.saveEmployeeSnapshotByNotChange(snapshotTime, userId, oemCode);
            startD = DateUtil.addDate(startD,1);
        }
    }

    /**
     * 删除用户快照
     */
    public void deleteEmployeeSnapshotByDate(String startDate,String endDate,Long userId,String oemCode){
        Date endD = DateUtil.parseDate(endDate +" 23:59:59","yyyy-MM-dd HH:mm:ss");
        Date yesterDay = DateUtil.parseDate(DateUtil.stampToDate(DateUtil.getYesterdayEndTime()),"yyyy-MM-dd HH:mm:ss");
        if(DateUtil.diffDate(yesterDay,endD) < 0 ){
            endDate = DateUtil.format(yesterDay,"yyyy-MM-dd HH:mm:ss");
        }
        this.mapper.deleteEmployeeSnapshotByDate(startDate,endDate,userId,oemCode);
    }
}

