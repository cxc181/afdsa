package com.yuqian.itax.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.user.dao.CustomerServiceWorkNumberMapper;
import com.yuqian.itax.user.entity.CustomerServiceWorkNumberEntity;
import com.yuqian.itax.user.entity.po.CustomerServiceWorkPO;
import com.yuqian.itax.user.entity.query.CustomerServiceWorkQuery;
import com.yuqian.itax.user.entity.vo.CustomerServiceWorkVO;
import com.yuqian.itax.user.service.CustomerServiceWorkNumberService;
import com.yuqian.itax.util.util.Md5Util;
import com.yuqian.itax.util.util.MemberPsdUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service("customerServiceWorkNumberService")
public class CustomerServiceWorkNumberServiceImpl extends BaseServiceImpl<CustomerServiceWorkNumberEntity,CustomerServiceWorkNumberMapper> implements CustomerServiceWorkNumberService {

    @Override
    public PageInfo<CustomerServiceWorkVO> customerServiceWorkPageInfo(CustomerServiceWorkQuery customerServiceWorkQuery) {
        PageHelper.startPage(customerServiceWorkQuery.getPageNumber(),customerServiceWorkQuery.getPageSize());
        return  new PageInfo<>(mapper.getCustomerServiceWorkList(customerServiceWorkQuery));
    }

    @Override
    public CustomerServiceWorkNumberEntity addCustomerServiceWorkNumberEntity(CustomerServiceWorkPO customerServiceWorkPO,Long userId) {
        //新增工号表信息
        CustomerServiceWorkNumberEntity customerServiceWorkNumberEntity=new CustomerServiceWorkNumberEntity();
        customerServiceWorkNumberEntity.setUserId(customerServiceWorkPO.getUserId());

        String slat= UUID.randomUUID().toString().replaceAll("-","");
        String pwd= MemberPsdUtil.encrypt( customerServiceWorkPO.getWorkNumberPwd(),customerServiceWorkPO.getWorkNumber(),slat);
        customerServiceWorkNumberEntity.setWorkNumber(customerServiceWorkPO.getWorkNumber());
        customerServiceWorkNumberEntity.setWorkNumberPwd(pwd);
        customerServiceWorkNumberEntity.setSlat(slat);
        customerServiceWorkNumberEntity.setWorkNumberName(customerServiceWorkPO.getWorkNumberName());
        customerServiceWorkNumberEntity.setStatus(1);
        customerServiceWorkNumberEntity.setAddTime(new Date());
        customerServiceWorkNumberEntity.setAddUser(String.valueOf(userId));
        mapper.insert(customerServiceWorkNumberEntity);
        return customerServiceWorkNumberEntity;
    }

    @Override
    public List<CustomerServiceWorkNumberEntity> queryCustomerServiceWorkNumberEntityByuserId(Long userId) {
        return mapper.queryCustomerServiceWorkNumberEntityByuserId(userId);
    }
}

