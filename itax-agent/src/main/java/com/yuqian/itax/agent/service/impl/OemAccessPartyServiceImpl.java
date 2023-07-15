package com.yuqian.itax.agent.service.impl;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.dao.OemAccessPartyMapper;
import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.entity.po.OemAccessPartyPO;
import com.yuqian.itax.agent.entity.query.OemAccessPartyQuery;
import com.yuqian.itax.agent.entity.vo.OemAccessPartyInfoVO;
import com.yuqian.itax.agent.entity.vo.OemAccessPartyVO;
import com.yuqian.itax.agent.service.OemAccessPartyService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service("oemAccessPartyService")
public class OemAccessPartyServiceImpl extends BaseServiceImpl<OemAccessPartyEntity,OemAccessPartyMapper> implements OemAccessPartyService {


    @Override
    public OemAccessPartyEntity findByCode(OemAccessPartyQuery query) {
        return mapper.queryByCode(query);
    }

    @Override
    public List<OemAccessPartyEntity> queryByOemCodeAndStatus(String oemCode, Integer status) {
        return mapper.queryByOemCodeAndStatus(oemCode,status);
    }

    @Override
    public List<OemAccessPartyInfoVO> queryByOemCode(String oemCode, Integer status) {
        return mapper.queryByOemCode(oemCode,status);
    }

    @Override
    public PageInfo<OemAccessPartyVO> queryOemAccessPartyPageInfo(OemAccessPartyQuery oemAccessPartyQuery) {
        PageHelper.startPage(oemAccessPartyQuery.getPageNumber(), oemAccessPartyQuery.getPageSize());
        return new  PageInfo<>(this.mapper.queryOemAccessPartyPageInfo(oemAccessPartyQuery));
    }

    @Override
    public void addOemAccessParty(OemAccessPartyPO po,String userName) {
        OemAccessPartyEntity entity = new OemAccessPartyEntity();
        entity.setAccessPartyCode(po.getAccessPartyCode().toUpperCase().trim());
        entity.setAccessPartyName(po.getAccessPartyName().trim());
        entity.setOemCode(po.getOemCode());
        String uuid = UUID.randomUUID().toString();
        entity.setAccessPartySecret(uuid.replaceAll("-", ""));
        // 默认上架
        entity.setStatus(1);
        entity.setAddTime(new Date());
        entity.setAddUser(userName);
        entity.setRemark(po.getRemark().trim());
        mapper.insertSelective(entity);
    }

    @Override
    public OemAccessPartyVO queryById(Long id) {
        return mapper.queryById(id);
    }

    @Override
    public OemAccessPartyEntity queryByOemCodeAndAccessPartyName(String oemCode, String accessPartyName, Long id) {
        return mapper.queryByOemCodeAndAccessPartyName(oemCode,accessPartyName,id);
    }

    @Override
    public OemAccessPartyEntity queryByAccessPartyCode(String oemAccessPartyCode) {
        return mapper.queryByAccessPartyCode(oemAccessPartyCode);
    }

    @Override
    public void updateOemAccessParty(OemAccessPartyPO po, String userName) {
        OemAccessPartyEntity entity = this.findById(po.getId());
        if (entity == null){
            throw  new BusinessException("id错误");
        }
        entity.setRemark(po.getRemark().trim());
        entity.setAccessPartyName(po.getAccessPartyName().trim());
        entity.setUpdateUser(userName);
        entity.setUpdateTime(new Date());
        mapper.updateByPrimaryKey(entity);
    }
}

