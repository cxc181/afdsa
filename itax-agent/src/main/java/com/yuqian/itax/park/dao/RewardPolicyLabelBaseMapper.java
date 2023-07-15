package com.yuqian.itax.park.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.RewardPolicyLabelBaseEntity;
import com.yuqian.itax.park.entity.vo.RewardPolicyLabelBaseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * dao
 * 
 * @Date: 2022年10月11日 11:09:27 
 * @author 蒋匿
 */
@Mapper
public interface RewardPolicyLabelBaseMapper extends BaseMapper<RewardPolicyLabelBaseEntity> {

    /**
     * 查询园区奖励政策标签列表
     * @return
     */
    List<RewardPolicyLabelBaseVO> queryList();

    /**
     * 根据奖励标签名称查询是否有该数据
     * @param rewardLabel
     * @return
     */
    RewardPolicyLabelBaseVO getRewardPolicyLabelOne(@Param("rewardLabel") String rewardLabel);

}

