package com.yuqian.itax.park.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.park.dao.ParkAgentAccountMapper;
import com.yuqian.itax.park.entity.ParkAgentAccountEntity;
import com.yuqian.itax.park.entity.query.ParkAgentAccountQuery;
import com.yuqian.itax.park.entity.vo.ParkAgentAccountPO;
import com.yuqian.itax.park.service.ParkAgentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;


@Service("parkAgentAccountService")
public class ParkAgentAccountServiceImpl extends BaseServiceImpl<ParkAgentAccountEntity,ParkAgentAccountMapper> implements ParkAgentAccountService {

    @Override
    public PageInfo<ParkAgentAccountEntity> queryParkAgentAccount(ParkAgentAccountQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return new PageInfo<>(mapper.queryParkAgentAccount(query));
    }

    @Override
    public List<ParkAgentAccountEntity> queryParkAgentAccountList(ParkAgentAccountQuery query) {
        return mapper.queryParkAgentAccount(query);
    }

    @Override
    public void updateParkAgentAccountStatus(Long id, Integer status,String account) {
        ParkAgentAccountEntity parkAgentAccountEntity=mapper.selectByPrimaryKey(id);
        if(null == parkAgentAccountEntity){
            throw  new BusinessException("经办人账号不存在。");
        }
        parkAgentAccountEntity.setStatus(status);
        parkAgentAccountEntity.setUpdateTime(new Date());
        parkAgentAccountEntity.setUpdateUser(account);
        mapper.updateByPrimaryKey(parkAgentAccountEntity);
    }

    @Override
    public ParkAgentAccountEntity addParkAgentAccount(ParkAgentAccountPO po,String account) {
        ParkAgentAccountEntity entity=new ParkAgentAccountEntity();
        entity.setAgentAccount(po.getAgentAccount());
        entity.setParkId(po.getParkId());
        List<ParkAgentAccountEntity> parkAgentAccountEntityList=mapper.select(entity);
        if(parkAgentAccountEntityList.size()>0){
            throw  new BusinessException("经办人账号已经存在");
        }
        ParkAgentAccountEntity parkAgentAccountEntity=new ParkAgentAccountEntity();
        parkAgentAccountEntity.setParkId(po.getParkId());
        parkAgentAccountEntity.setStatus(1);
        parkAgentAccountEntity.setAgentAccount(po.getAgentAccount());
        parkAgentAccountEntity.setAgentName(po.getAgentName());
        parkAgentAccountEntity.setIdCardNo(po.getIdCardNo());
        parkAgentAccountEntity.setIdCardFront(po.getIdCardFront());
        parkAgentAccountEntity.setIdCardBack(po.getIdCardBack());
        parkAgentAccountEntity.setAddTime(new Date());
        parkAgentAccountEntity.setAddUser(account);
        mapper.insert(parkAgentAccountEntity);
        return parkAgentAccountEntity;
    }

    @Override
    public ParkAgentAccountEntity queryParkAgentAccountByAgentAccount(Long parkId, String agentAccount, Integer status) {
        return mapper.queryParkAgentAccountByAgentAccount(parkId, agentAccount, status);
    }
}

