package com.yuqian.itax.user.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.AgentProfitsRulesMapper;
import com.yuqian.itax.user.entity.AgentProfitsRulesEntity;
import com.yuqian.itax.user.service.AgentProfitsRulesService;
import org.springframework.stereotype.Service;


@Service("agentProfitsRulesService")
public class AgentProfitsRulesServiceImpl extends BaseServiceImpl<AgentProfitsRulesEntity, AgentProfitsRulesMapper> implements AgentProfitsRulesService {

    @Override
    public AgentProfitsRulesEntity queryAgentProfitsRulesEntityByAgentIdAndAgentType(Long agentId, Integer agentType) {
        return mapper.queryAgentProfitsRulesEntityByAgentIdAndAgentType(agentId,agentType);
    }

    @Override
    public AgentProfitsRulesEntity queryAgentProfitsRulesEntityByOemCodeAndAgentType(String oemCode,Integer agentType) {
        return mapper.queryAgentProfitsRulesEntityByOemCodeAndAgentType(oemCode,agentType);
    }
}

