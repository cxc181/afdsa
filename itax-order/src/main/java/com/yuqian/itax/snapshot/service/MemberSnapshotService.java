package com.yuqian.itax.snapshot.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.snapshot.entity.MemberSnapshotEntity;
import com.yuqian.itax.snapshot.dao.MemberSnapshotMapper;
import com.yuqian.itax.snapshot.entity.query.MemberSnapshotQuery;
import com.yuqian.itax.snapshot.entity.vo.MemberSnapshotVO;

import java.util.List;

/**
 * 会员快照表service
 * 
 * @Date: 2020年10月26日 11:24:47 
 * @author 蒋匿
 */
public interface MemberSnapshotService extends IBaseService<MemberSnapshotEntity,MemberSnapshotMapper> {

    /**
     * 用户数统计
     */

    List<MemberSnapshotVO> queryMemberSnapshotUser(MemberSnapshotQuery query);

    /**
     * 新增或修复会员用户快照
     */
    void updateOrMemberSnapshot(String startDate,String endDate,String oemCode);
    /**
     * 删除
     */
    void deleteMemberSnapshotByDate(String startDate,String endDate,String oemCode);
    /**
     * 修改会员快照表账号
     */
    void updateMemberAccountByMemberId(String memberAccount,String remark,Long memberId);
}

