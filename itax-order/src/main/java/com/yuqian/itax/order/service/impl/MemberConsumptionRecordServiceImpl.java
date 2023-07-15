package com.yuqian.itax.order.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.dao.MemberConsumptionRecordMapper;
import com.yuqian.itax.order.entity.ConsumptionInvoiceOrderEntity;
import com.yuqian.itax.order.entity.MemberConsumptionRecordEntity;
import com.yuqian.itax.order.entity.query.MemberConsumptionRecordQuery;
import com.yuqian.itax.order.entity.query.ConsumptionRecordQuery;
import com.yuqian.itax.order.entity.vo.ConsumptionRecordVO;
import com.yuqian.itax.order.entity.vo.MemberConsumptionRecordVO;
import com.yuqian.itax.order.service.ConsumptionInvoiceOrderService;
import com.yuqian.itax.order.service.MemberConsumptionRecordService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Date;
import java.util.List;

@Slf4j
@Service("memberConsumptionRecordService")
public class MemberConsumptionRecordServiceImpl extends BaseServiceImpl<MemberConsumptionRecordEntity,MemberConsumptionRecordMapper> implements MemberConsumptionRecordService {

    @Autowired
    ConsumptionInvoiceOrderService consumptionInvoiceOrderService;

    @Override
    public PageInfo<MemberConsumptionRecordVO> queryMemberConsumptionRecordPage(MemberConsumptionRecordQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity=consumptionInvoiceOrderService.findById(query.getId());
        String orderNo="'"+consumptionInvoiceOrderEntity.getConsumptionOrderRela().replace(",","','")+"'";
        query.setOrderNo(orderNo);
        return new PageInfo<>(mapper.queryMemberConsumptionRecordList(query));
    }

    @Override
    public List<MemberConsumptionRecordVO> queryMemberConsumptionRecordList(MemberConsumptionRecordQuery query) {
        ConsumptionInvoiceOrderEntity consumptionInvoiceOrderEntity=consumptionInvoiceOrderService.findById(query.getId());
        String orderNo="'"+consumptionInvoiceOrderEntity.getConsumptionOrderRela().replace(",","','")+"'";
        query.setOrderNo(orderNo);
        return mapper.queryMemberConsumptionRecordList(query);
    }
    @Autowired
    private MemberAccountService memberAccountService;

    @Override
    public List<ConsumptionRecordVO> listConsumptionRecord(Long currUserId, String oemCode, ConsumptionRecordQuery query) throws BusinessException {
        log.info("获取可开票的订单列表:{},{},{}",currUserId,oemCode, JSON.toJSONString(query));
        // 查询会员是否存在
        MemberAccountEntity member = this.memberAccountService.findById(currUserId);
        if(null == member){
            throw new BusinessException("当前会员不存在");
        }
        query.setOemCode(oemCode);
        query.setCurrUserId(currUserId);
        if(null != query.getOrderType() && query.getOrderType().intValue() == -1){
            query.setOrderType(null);// 查询全部
        }

        // 处理查询条件，时间格式化处理 YYYY-MM-DD HH:mm:ss
        if(StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isBlank(query.getEndDate())){// 开始时间不为空，结束时间为空
            query.setDay(query.getStartDate());
        } else if(StringUtils.isNotBlank(query.getEndDate()) && StringUtils.isBlank(query.getStartDate())){// 结束时间不为空，开始时间为空
            query.setDay(query.getEndDate());
        } else if (StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isNotBlank(query.getEndDate())){ // 开始时间结束时间均不为空
            query.setStartDate(query.getStartDate() + " 00:00:00");
            query.setEndDate(query.getEndDate() + " 23:59:59");
        }
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        List<ConsumptionRecordVO> list = this.mapper.listConsumptionRecord(query);
        return list;
    }

    @Override
    public void insertSelective(String oemCode, String orderNo, Integer orderType, Long memberId, Long consumptionAmount, String addUser, String remark) {
        MemberConsumptionRecordEntity entity = new MemberConsumptionRecordEntity();
        entity.setOrderNo(orderNo);
        entity.setOrderType(orderType);
        entity.setMemberId(memberId);
        entity.setConsumptionAmount(consumptionAmount);
        entity.setOemCode(oemCode);
        entity.setIsOpenInvoice(0);
        entity.setAddUser(addUser);
        entity.setAddTime(new Date());
        entity.setRemark(remark);
        mapper.insertSelective(entity);
    }

    @Override
    public void updateRecordByOrderNo(String orderNos) throws BusinessException {
        log.info("根据订单号批量更新会员消费记录（是否已开票）:{}",orderNos);
        this.mapper.updateRecordByOrderNo(orderNos);
    }
}

