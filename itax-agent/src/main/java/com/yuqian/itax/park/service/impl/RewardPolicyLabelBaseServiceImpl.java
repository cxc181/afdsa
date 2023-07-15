package com.yuqian.itax.park.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.park.dao.RewardPolicyLabelBaseMapper;
import com.yuqian.itax.park.entity.RewardPolicyLabelBaseEntity;
import com.yuqian.itax.park.entity.vo.RewardPolicyLabelBaseVO;
import com.yuqian.itax.park.service.RewardPolicyLabelBaseService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("rewardPolicyLabelBaseService")
public class RewardPolicyLabelBaseServiceImpl extends BaseServiceImpl<RewardPolicyLabelBaseEntity,RewardPolicyLabelBaseMapper> implements RewardPolicyLabelBaseService {


    /**
     * 查询园区奖励政策标签列表
     * @return
     */
    @Override
    public List<RewardPolicyLabelBaseVO> queryList() {
        List<RewardPolicyLabelBaseVO> list = this.mapper.queryList();
        return list;
    }

}

