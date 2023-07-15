package com.yuqian.itax.snapshot.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.snapshot.dao.UserSnapshotMapper;
import com.yuqian.itax.snapshot.entity.UserSnapshotEntity;
import com.yuqian.itax.snapshot.service.UserSnapshotService;
import org.springframework.stereotype.Service;


@Service("userSnapshotService")
public class UserSnapshotServiceImpl extends BaseServiceImpl<UserSnapshotEntity,UserSnapshotMapper> implements UserSnapshotService {

    @Override
    public void updateOrUserSnapshot(String startDate, String endDate, String oemCode) {
        mapper.updateOrInsertUserSnapshot(startDate,endDate,oemCode);
    }

    @Override
    public void deleteUserSnapshotByDate(String startDate, String endDate, String oemCode) {
        mapper.deleteUserSnapshotByDate(startDate,endDate,oemCode);
    }
}

