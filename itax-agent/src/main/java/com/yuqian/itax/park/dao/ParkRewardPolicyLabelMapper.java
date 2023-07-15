package com.yuqian.itax.park.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.park.entity.ParkRewardPolicyLabelEntity;
import com.yuqian.itax.park.entity.query.ParkRewardPolicyLabelQuery;
import com.yuqian.itax.park.entity.vo.ParkRewardPolicyLabelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * dao
 * 
 * @Date: 2022年10月11日 11:08:41 
 * @author 蒋匿
 */
@Mapper
public interface ParkRewardPolicyLabelMapper extends BaseMapper<ParkRewardPolicyLabelEntity> {


    /**
     * 根据园区id查询对应的奖励政策
     * @param query
     * @return
     */
    List<ParkRewardPolicyLabelVO> queryList(@Param("query") ParkRewardPolicyLabelQuery query);

    /**
     * 根据机构编码查询奖励标签
     * @param oemCode
     * @return
     */
    List<String> queryLabelByOemCode(String oemCode);

    /**
     * 验证园区里奖励标签唯一
     * @param rewardLabel
     * @return
     */
    int verifyRewardLabelPark(@Param("parkId") Long parkId,@Param("rewardLabel")String rewardLabel);

}

