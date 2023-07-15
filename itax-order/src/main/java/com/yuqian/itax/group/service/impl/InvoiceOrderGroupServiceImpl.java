package com.yuqian.itax.group.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.group.dao.GroupPaymentAnalysisRecordMapper;
import com.yuqian.itax.group.dao.InvoiceOrderGroupMapper;
import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
import com.yuqian.itax.group.entity.query.InvoiceOrderGroupQuery;
import com.yuqian.itax.group.entity.vo.InvoiceOrderGroupAuditVo;
import com.yuqian.itax.group.entity.vo.InvoiceOrderGroupListVO;
import com.yuqian.itax.group.enums.InvoiceOrderGroupStatusEnum;
import com.yuqian.itax.group.service.InvoiceOrderGroupService;
import com.yuqian.itax.order.dao.InvoiceOrderChangeRecordMapper;
import com.yuqian.itax.order.dao.InvoiceOrderMapper;
import com.yuqian.itax.order.dao.OrderMapper;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.InvoiceRecordEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.vo.InvoiceOrderByGroupOrderNoVO;
import com.yuqian.itax.order.entity.vo.InvoiceSumInfoVO;
import com.yuqian.itax.order.entity.vo.OpenOrderVO;
import com.yuqian.itax.order.enums.InvoiceOrderStatusEnum;
import com.yuqian.itax.order.enums.InvoiceRecordStatusEnum;
import com.yuqian.itax.order.service.InvoiceRecordService;
import com.yuqian.itax.user.dao.CompanyInvoiceRecordMapper;
import com.yuqian.itax.user.entity.*;
import com.yuqian.itax.user.service.CompanyTaxHostingService;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.user.service.MemberCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


@Service("invoiceOrderGroupService")
@Slf4j
public class InvoiceOrderGroupServiceImpl extends BaseServiceImpl<InvoiceOrderGroupEntity,InvoiceOrderGroupMapper> implements InvoiceOrderGroupService {

    @Resource
    private InvoiceOrderMapper invoiceOrderMapper;

    @Resource
    private InvoiceOrderChangeRecordMapper invoiceOrderChangeRecordMapper;

    @Autowired
    private CompanyInvoiceRecordMapper companyInvoiceRecordMapper;

    @Autowired
    private GroupPaymentAnalysisRecordMapper groupPaymentAnalysisRecordMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private InvoiceRecordService invoiceRecordService;

    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    MemberAccountService memberAccountService;

    @Autowired
    CompanyTaxHostingService companyTaxHostingService;


    @Override
    public PageInfo<InvoiceOrderGroupListVO> listPage(InvoiceOrderGroupQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(listInvoiceOrderGroup(query));
    }

    @Override
    public List<InvoiceOrderGroupListVO> listInvoiceOrderGroup(InvoiceOrderGroupQuery query) {
        return mapper.listInvoiceOrderGroup(query);
    }

    @Override
    public InvoiceOrderGroupAuditVo queryInvoiceOrderByOrderNo(String orderNo) {
        return mapper.queryInvoiceOrderByOrderNo(orderNo);
    }

    @Override
    public InvoiceOrderGroupEntity queryByOrderNo(String orderNo, String oemCode) {
        return mapper.queryByOrderNo(orderNo, oemCode);
    }

    @Override
    @Transactional
    public void updateStatus(InvoiceOrderGroupEntity entity, Integer status, UserEntity userEntity) {
        Date date = new Date();
        String remark;
        if (Objects.equals(status, 1)) {
            //判断该批量开票订单下的开票记录状态是否都为已完成
            List<InvoiceRecordEntity> invoiceRecordEntityList=invoiceRecordService.queryGroupInvoiceOrderByGroupOrderNo(entity.getOrderNo(),null, InvoiceRecordStatusEnum.COMPLETED.getValue()+"");
            if(CollectionUtil.isNotEmpty(invoiceRecordEntityList)){
                throw  new BusinessException("该集团开票订单存在开票记录未完成的订单，请全部出票后再操作");
            }
            //签收
            remark = "集团开票订单签收";
            //集团开票主订单
            mapper.updateStatusByOrderNo(entity.getOrderNo(), entity.getOemCode(), InvoiceOrderGroupStatusEnum.SIGNED.getValue(), userEntity.getUsername(), date, remark);
            //开票子订单主表
            orderMapper.updateStatusByGroupOrderNo(entity.getOrderNo(), entity.getOemCode(), InvoiceOrderStatusEnum.SIGNED.getValue(), userEntity.getUsername(), date);
            //开票子订单开票表
            InvoiceOrderEntity invoiceOrderEntity = new InvoiceOrderEntity();
            invoiceOrderEntity.setGroupOrderNo(entity.getOrderNo());
            invoiceOrderEntity.setOemCode(entity.getOemCode());
            invoiceOrderEntity.setCompleteTime(date);
            invoiceOrderEntity.setUpdateUser(userEntity.getUsername());
            invoiceOrderEntity.setUpdateTime(date);
            invoiceOrderMapper.updateByGroupOrderNo(invoiceOrderEntity);
            //开票子订单历史记录
            invoiceOrderChangeRecordMapper.batchAddByGroupOrderNo(entity.getOrderNo(), entity.getOemCode(), InvoiceOrderStatusEnum.SIGNED.getValue(), userEntity.getUsername(), date, remark);
            // 根据集团订单编号查询
            List<InvoiceOrderByGroupOrderNoVO> list = invoiceOrderMapper.listByGroupOrderNoInfo(entity.getOrderNo());
            //  V3.0 订单推送国金
            List<OpenOrderVO> listToBePush = new ArrayList<OpenOrderVO>();
            if (list != null && list.size()>0){
                for(InvoiceOrderByGroupOrderNoVO vo:list){
                    OrderEntity orderEntity = orderMapper.queryByOrderNo(vo.getOrderNo());
                    OpenOrderVO orderInfo = new OpenOrderVO();
                    orderInfo.setOrderNo(vo.getOrderNo());
                    orderInfo.setId(orderEntity.getUserId());
                    orderInfo.setOemCode(orderEntity.getOemCode());
                    orderInfo.setOrderType(orderEntity.getOrderType());
                    listToBePush.add(orderInfo);
                }
                rabbitTemplate.convertAndSend("orderPush", listToBePush);
            }
        } else {
            //查询开票记录
            String notOrderStatus = InvoiceOrderStatusEnum.CREATED.getValue() + "," + InvoiceOrderStatusEnum.CANCELED.getValue();
            List<InvoiceOrderEntity> lists = invoiceOrderMapper.queryByGroupOrderNo(entity.getOrderNo(), entity.getOemCode(), notOrderStatus);
            if (CollectionUtil.isNotEmpty(lists)) {
                for (InvoiceOrderEntity invOrder : lists) {
                    //修改企业开票额度
                    companyInvoiceRecordMapper.refund(invOrder.getCompanyId(), invOrder.getAddTime(), invOrder.getInvoiceAmount(), userEntity.getUsername(), date);
                }
            }
            //取消
            remark = "集团开票订单取消";
            //集团开票主订单
            mapper.updateStatusByOrderNo(entity.getOrderNo(), entity.getOemCode(), InvoiceOrderGroupStatusEnum.CANCELED.getValue(), userEntity.getUsername(), date, remark);
            //开票子订单
            orderMapper.updateStatusByGroupOrderNo(entity.getOrderNo(), entity.getOemCode(), InvoiceOrderStatusEnum.CANCELED.getValue(), userEntity.getUsername(), date);
            //开票子订单历史记录
            invoiceOrderChangeRecordMapper.batchAddByGroupOrderNo(entity.getOrderNo(), entity.getOemCode(), InvoiceOrderStatusEnum.CANCELED.getValue(), userEntity.getUsername(), date, remark);
            //状态为待提交、出票失败的开票记录都变为已取消其他得变为已完成
            //已取消
            invoiceRecordService.updateInvoiceRecordStatusByGroupOrderNoAndStatuss(entity.getOrderNo(),InvoiceRecordStatusEnum.CANCELED.getValue(),"集团开票取消订单",userEntity.getUsername(),new Date(),InvoiceRecordStatusEnum.TO_SUBMIT.getValue()+","+InvoiceRecordStatusEnum.THE_DRAWER_FAILURE.getValue(),null);
            //已完成
            invoiceRecordService.updateInvoiceRecordStatusByGroupOrderNoAndStatuss(entity.getOrderNo(),InvoiceRecordStatusEnum.COMPLETED.getValue(),"集团开票取消订单",userEntity.getUsername(),new Date(),null,InvoiceRecordStatusEnum.TO_SUBMIT.getValue()+","+InvoiceRecordStatusEnum.THE_DRAWER_FAILURE.getValue());

        }
    }

    @Override
    public List<InvoiceOrderGroupEntity> queryByStatus(Integer orderStatus) {
        return mapper.queryByStatus(orderStatus);
    }

    @Override
    public void ticketing(Long id) {
        try {
            InvoiceOrderGroupEntity entity = mapper.selectByPrimaryKey(id);
            if (entity == null) {
                return;
            }
            if (!Objects.equals(entity.getOrderStatus(), InvoiceOrderGroupStatusEnum.CREATED.getValue())) {
                //状态不对
                return;
            }
            //解析结果 0-解析中 1-解析成功 2-解析失败
            int result = groupPaymentAnalysisRecordMapper.countByStatus(entity.getOrderNo(), entity.getOemCode(), 0);
            if (result > 0) {
                //存在解析中的数据，不予处理
                return;
            }
            result = groupPaymentAnalysisRecordMapper.countByStatus(entity.getOrderNo(), entity.getOemCode(), 1);
            if (result <= 0) {
                //没有成功解析数据修改集团开票主订单为已取消
                mapper.updateStatusByOrderNo(entity.getOrderNo(), entity.getOemCode(), InvoiceOrderGroupStatusEnum.CANCELED.getValue(), entity.getAddUser(), new Date(), "没有成功解析流水");
                return;
            }
            //存在解析成功数据
            //统计开票信息
            InvoiceSumInfoVO vo = invoiceOrderMapper.sumInvoiceInfo(entity.getOrderNo(), entity.getOemCode(), InvoiceOrderStatusEnum.UNCHECKED.getValue());
            //修改集团开票主订单
            if (vo != null) {
                entity.setInvoiceAmount(vo.getInvoiceAmount());
                entity.setServiceFee(vo.getServiceFee());
                entity.setServiceFeeDiscount(vo.getServiceFeeDiscount());
                entity.setPersonalIncomeTax(vo.getPersonalIncomeTax());
                entity.setVatFee(vo.getVatFee());
                entity.setVatPayment(vo.getVatPayment());
                entity.setSurcharge(vo.getSurcharge());
                entity.setSurchargePayment(vo.getSurchargePayment());
                entity.setIncomeTaxPayment(vo.getIncomeTaxPayment());
            }
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(entity.getAddUser());
            entity.setOrderStatus(InvoiceOrderGroupStatusEnum.EXAMINE.getValue());
            mapper.updateByPrimaryKeySelective(entity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void approved(InvoiceOrderGroupQuery query) {
        InvoiceOrderGroupEntity groupEntity = mapper.queryOrderByOrderNo(query.getLikeOrderNo());
        if (query.getPayImgUrl() != null) {
            groupEntity.setPayImgUrl(query.getPayImgUrl());
        }
        if (query.getAuditDesc() != null){
            groupEntity.setAuditDesc(query.getAuditDesc());
        }
        //  集团开票订单为出票中
        groupEntity.setOrderStatus(InvoiceOrderGroupStatusEnum.IN_TICKETING.getValue());
        mapper.updateByPrimaryKeySelective(groupEntity);
        //
        List<InvoiceOrderByGroupOrderNoVO> list =  invoiceOrderMapper.listByGroupOrderNo(groupEntity.getOrderNo(),groupEntity.getOemCode());
        if (list != null && list.size()>0){
            for (InvoiceOrderByGroupOrderNoVO vo:list){
                InvoiceOrderEntity invoice = invoiceOrderMapper.queryByOrderNo(vo.getOrderNo());
                //  子订单状态设置为出票中
                OrderEntity orderEntity = orderMapper.queryByOrderNo(vo.getOrderNo());
                orderEntity.setOrderStatus(InvoiceOrderStatusEnum.IN_TICKETING.getValue());
                orderMapper.updateByPrimaryKeySelective(orderEntity);
                MemberCompanyEntity memberCompanyEntity = memberCompanyService.findById(invoice.getCompanyId());
                MemberAccountEntity member=memberAccountService.findById(memberCompanyEntity.getMemberId());
                //查询企业托管信息
                CompanyTaxHostingEntity companyTaxHostingEntity = companyTaxHostingService.getCompanyTaxHostingByCompanyId(memberCompanyEntity.getId(),1);
                //创建开票记录
                invoiceRecordService.createInvoiceRecord(invoice,companyTaxHostingEntity,memberCompanyEntity.getEin(), memberCompanyEntity.getParkId(), member.getId(), member.getMemberAccount(),true);
            }
        }
    }


}

