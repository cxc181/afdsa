package com.yuqian.itax.snapshot.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.snapshot.dao.MemberSnapshotMapper;
import com.yuqian.itax.snapshot.entity.MemberSnapshotEntity;
import com.yuqian.itax.snapshot.entity.query.MemberSnapshotQuery;
import com.yuqian.itax.snapshot.entity.vo.MemberSnapshotVO;
import com.yuqian.itax.snapshot.service.MemberSnapshotService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("memberSnapshotService")
public class MemberSnapshotServiceImpl extends BaseServiceImpl<MemberSnapshotEntity,MemberSnapshotMapper> implements MemberSnapshotService {

    @Override
    public List<MemberSnapshotVO> queryMemberSnapshotUser(MemberSnapshotQuery query) {
        return mapper.queryMemberSnapshotUser(query);
    }

    @Override
    public void updateOrMemberSnapshot(String startDate, String endDate, String oemCode) {
        mapper.updateOrMemberSnapshot(startDate,endDate,oemCode);
    }

    @Override
    public void deleteMemberSnapshotByDate(String startDate, String endDate, String oemCode) {
        mapper.deleteMemberSnapshotByDate(startDate,endDate,oemCode);
    }

    @Override
    public void updateMemberAccountByMemberId(String memberAccount, String remark, Long memberId) {
        mapper.updateMemberAccountByMemberId(memberAccount,remark,memberId);
    }
}

