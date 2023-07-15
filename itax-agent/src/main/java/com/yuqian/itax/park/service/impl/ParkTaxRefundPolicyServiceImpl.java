package com.yuqian.itax.park.service.impl;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.park.dao.ParkTaxRefundPolicyMapper;
import com.yuqian.itax.park.entity.ParkTaxRefundPolicyEntity;
import com.yuqian.itax.park.entity.ParkTaxRefundPolicyPO;
import com.yuqian.itax.park.entity.vo.ParkTaxRefundPolicyVO;
import com.yuqian.itax.park.service.ParkTaxRefundPolicyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service("parkTaxRefundPolicyService")
public class ParkTaxRefundPolicyServiceImpl extends BaseServiceImpl<ParkTaxRefundPolicyEntity, ParkTaxRefundPolicyMapper> implements ParkTaxRefundPolicyService {

    @Resource
    private ParkTaxRefundPolicyMapper parkTaxRefundPolicyMapper;


    /**
     * 根据园区id查询园区返税政策详情
     * @param parkId
     * @return
     */
    @Override
    public List<ParkTaxRefundPolicyVO> info(Long parkId) {
        List<ParkTaxRefundPolicyVO> list = parkTaxRefundPolicyMapper.queryList(parkId);
        return list;
    }

    /**
     * 修改园区返税政策
     * @param poList
     * @param currUser
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(List<ParkTaxRefundPolicyPO> poList, CurrUser currUser) {
        if(CollectionUtil.isEmpty(poList)){
            throw  new BusinessException("返税政策数据不能为空");
        }
        List<ParkTaxRefundPolicyEntity> insertList = new ArrayList<>();
        List<ParkTaxRefundPolicyEntity> updateList = new ArrayList<>();
        for(ParkTaxRefundPolicyPO po:poList){
            if(po.getId() != null){
                // 执行修改
                ParkTaxRefundPolicyEntity entity = parkTaxRefundPolicyMapper.selectByPrimaryKey(po.getId());
                BeanUtils.copyProperties(po,entity);
                entity.setUpdateTime(new Date());
                entity.setUpdateUser(currUser.getUseraccount());
                updateList.add(entity);
            }else{
                // 否则新增数据
                ParkTaxRefundPolicyEntity entity = new ParkTaxRefundPolicyEntity();
                BeanUtils.copyProperties(po,entity);
                entity.setAddTime(new Date());
                entity.setAddUser(currUser.getUseraccount());
                insertList.add(entity);
            }
        }


        if(CollectionUtil.isNotEmpty(insertList)){
            // 根据园区id先删除原来对应的数据
            //parkTaxRefundPolicyMapper.deletePolicyAll(poList.get(0).getParkId());
            insertList.forEach(e->{
                parkTaxRefundPolicyMapper.insertSelective(e);
            });
        }
        if(CollectionUtil.isNotEmpty(updateList)){
            updateList.forEach(e->{
                parkTaxRefundPolicyMapper.updateByPrimaryKey(e);
            });
        }
    }


}

