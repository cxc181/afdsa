package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.dao.AgentProfitsRulesMapper;
import com.yuqian.itax.user.entity.AgentProfitsRulesEntity;

/**
 * 代理商分润规则service
 * 
 * @Date: 2019年12月12日 17:12:52 
 * @author 蒋匿
 */
public interface AgentProfitsRulesService extends IBaseService<AgentProfitsRulesEntity, AgentProfitsRulesMapper> {
    /**
     * 根据agentId和agentType查询分润规则表
     */
    AgentProfitsRulesEntity queryAgentProfitsRulesEntityByAgentIdAndAgentType(Long agentId,Integer agentType);

    /**
     * 根据OEMCODE
     */
    AgentProfitsRulesEntity queryAgentProfitsRulesEntityByOemCodeAndAgentType(String oemCode,Integer agentType);
}

