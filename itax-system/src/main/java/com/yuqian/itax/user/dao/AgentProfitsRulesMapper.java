package com.yuqian.itax.user.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.user.entity.AgentProfitsRulesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 代理商分润规则dao
 * 
 * @Date: 2019年12月12日 17:12:52 
 * @author 蒋匿
 */
@Mapper
public interface AgentProfitsRulesMapper extends BaseMapper<AgentProfitsRulesEntity> {
    /**
     * 根据UserId获取代理商分润信息
     */
    AgentProfitsRulesEntity getAgentProfitsRulesEntityByAgentId(@Param("agentId")Long agentId);

    AgentProfitsRulesEntity queryAgentProfitsRulesEntityByAgentIdAndAgentType(@Param("agentId")Long agentId,@Param("agentType") Integer agentType);

    AgentProfitsRulesEntity queryAgentProfitsRulesEntityByOemCodeAndAgentType(@Param("oemCode") String oemCode,@Param("agentType") Integer agentType);
}

