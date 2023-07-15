package com.yuqian.itax.snapshot.dao;

import com.yuqian.itax.snapshot.entity.MemberSnapshotEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.snapshot.entity.query.MemberSnapshotQuery;
import com.yuqian.itax.snapshot.entity.vo.MemberSnapshotVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员快照表dao
 * 
 * @Date: 2020年10月26日 11:24:47 
 * @author 蒋匿
 */
@Mapper
public interface MemberSnapshotMapper extends BaseMapper<MemberSnapshotEntity> {

    /**
     * 用户数统计
     */
    List<MemberSnapshotVO> queryMemberSnapshotUser(MemberSnapshotQuery query);
    /**
     *
     */
    void updateOrMemberSnapshot(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("oemCode") String oemCode);
    /**
     * 删除
     */
    void deleteMemberSnapshotByDate(@Param("startDate") String startDate,@Param("endDate") String endDate,@Param("oemCode") String oemCode );
    /**
     * 修改用户账号
     */
    void updateMemberAccountByMemberId(@Param("memberAccount")  String memberAccount,@Param("remark")  String remark,@Param("memberId")  Long memberId);
}

