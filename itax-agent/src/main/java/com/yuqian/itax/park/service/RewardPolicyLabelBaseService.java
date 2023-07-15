package com.yuqian.itax.park.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.park.dao.RewardPolicyLabelBaseMapper;
import com.yuqian.itax.park.entity.RewardPolicyLabelBaseEntity;
import com.yuqian.itax.park.entity.vo.RewardPolicyLabelBaseVO;

import java.util.List;

/**
 * service
 * 
 * @Date: 2022年10月11日 11:09:27 
 * @author 蒋匿
 */
public interface RewardPolicyLabelBaseService extends IBaseService<RewardPolicyLabelBaseEntity,RewardPolicyLabelBaseMapper> {

    /**
     * 查询园区奖励政策标签列表
     * @return
     */
    List<RewardPolicyLabelBaseVO> queryList();

}

