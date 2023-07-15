package com.yuqian.itax.pay.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.ObjectUtil;
import com.yuqian.itax.order.dao.OrderMapper;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.enums.RACWStatusEnum;
import com.yuqian.itax.order.enums.RegOrderStatusEnum;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.pay.dao.PayWaterMapper;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.entity.query.PayWaterQuery;
import com.yuqian.itax.pay.entity.query.WthdrawQuery;
import com.yuqian.itax.pay.entity.vo.PaywaterVO;
import com.yuqian.itax.pay.entity.vo.WithdrawVO;
import com.yuqian.itax.pay.enums.PayWaterStatusEnum;
import com.yuqian.itax.pay.enums.PayWaterTypeEnum;
import com.yuqian.itax.pay.enums.RefundWaterStatusEnum;
import com.yuqian.itax.pay.service.PayWaterService;
import com.yuqian.itax.user.enums.AuditStateEnum;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.util.util.UniqueNumGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service("payWaterService")
public class PayWaterServiceImpl extends BaseServiceImpl<PayWaterEntity, PayWaterMapper> implements PayWaterService {
    @Resource
    private PayWaterMapper payWaterMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OemService oemService;
    @Resource
    private OrderMapper orderMapper;
    @Autowired
    private UserCapitalAccountService userCapitalAccountService;

    @Override
    public void updatePayWater(PayWaterEntity orderPay) {
        this.payWaterMapper.updatePayWater(orderPay);
    }

    @Override
    public int insertSelective(PayWaterEntity entity) {
        if (StringUtil.isBlank(entity.getOemCode())) {
            throw new BusinessException("机构编码为空");
        }
        // 查询机构
        OemEntity oem = Optional.ofNullable(oemService.getOem(entity.getOemCode())).orElseThrow(() -> new BusinessException("未查询到机构信息"));
        if (oem.getIsOtherOemPay() == 1) {
            entity.setOtherPayOemcode(oem.getOtherPayOemcode());
        } else {
            entity.setOtherPayOemcode(oem.getOemCode());
        }

        return mapper.insertSelective(entity);
    }

    @Override
    public PageInfo<WithdrawVO> withdrawPageInfo(WthdrawQuery wthdrawQuery) {
        PageHelper.startPage(wthdrawQuery.getPageNumber(), wthdrawQuery.getPageSize());
        return new PageInfo<>(this.mapper.getWithdrawPayWaterList(wthdrawQuery));
    }

    @Override
    public List<WithdrawVO> withdrawList(WthdrawQuery wthdrawQuery) {
        return this.mapper.getWithdrawPayWaterList(wthdrawQuery);
    }

    @Override
    public PageInfo<PayWaterEntity> listPagePayWater(PayWaterQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo(listPayWater(query));
    }

    @Override
    public List<PaywaterVO> listPayWater(PayWaterQuery query) {
        return mapper.listPayWater(query);
    }

    @Override
    public PageInfo<PaywaterVO> payWaterPageInfo(PayWaterQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<>(mapper.getPayWaterList(query));
    }

    @Override
    public List<PaywaterVO> payWaterList(PayWaterQuery query) {
            return mapper.getPayWaterList(query);
    }

    @Override
    public List<PayWaterEntity> selectPayingList(Integer payWay) {
        return payWaterMapper.selectPayingList(payWay);
    }
    @Override
    public List<PayWaterEntity> selectRefundPayingList(Integer payWay, Integer orderBy) {
        return payWaterMapper.selectRefundPayingList(payWay, orderBy);
    }
    @Override
    public void updatePayStatus(PayWaterEntity water) {
        this.payWaterMapper.updatePayStatus(water);
    }

    @Override
    public void updatePayStatusByPayNo(String payNo, String updateUser,Integer payStatus,String upStatusCode,String upResultMsg) {
        PayWaterEntity payWater = new PayWaterEntity();
        payWater.setPayStatus(payStatus);
        payWater.setUpdateUser(updateUser);
        payWater.setUpdateTime(new Date());
        payWater.setPayNo(payNo);
        payWater.setUpStatusCode(upStatusCode);
        payWater.setUpResultMsg(upResultMsg);
        this.updatePayWater(payWater);
    }

    @Override
    @Transactional
    public void updateWaterAndOrder(PayWaterEntity payWaterEntity, OrderEntity orderEntity, Integer auditStatus, String payWaterImgs, String auditRemark, String updateUser) {
        Date date = new Date();
        orderEntity.setAuditRemark(auditRemark);
        orderEntity.setUpdateTime(date);
        orderEntity.setUpdateUser(updateUser);
        payWaterEntity.setUpdateTime(date);
        payWaterEntity.setUpdateUser(updateUser);
        if (Objects.equals(auditStatus, 1)) {
            //审核通过
            orderEntity.setPayWaterImgs(payWaterImgs);
            orderEntity.setAuditStatus(AuditStateEnum.APPROVED.getValue());
            orderEntity.setOrderStatus(RACWStatusEnum.PAYED.getValue());
            payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
            payWaterEntity.setPayTime(date);
            //删除冻结款
            UserCapitalAccountEntity accEntity = userCapitalAccountService.queryByUserIdAndType(payWaterEntity.getMemberId(), payWaterEntity.getUserType(), payWaterEntity.getOemCode(), payWaterEntity.getWalletType());
            if (accEntity == null) {
                throw new BusinessException("资金账户不存在");
            }
            userCapitalAccountService.delFreezeBalance(accEntity, orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getPayAmount(),updateUser);
        } else {
            orderEntity.setAuditStatus(AuditStateEnum.APPROVE_NO_PASS.getValue());
            orderEntity.setOrderStatus(RACWStatusEnum.AUDIT_FAIL.getValue());
            // 支付流水状态  变成支付失败
            payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
            //资金解冻
            UserCapitalAccountEntity accEntity = userCapitalAccountService.queryByUserIdAndType(payWaterEntity.getMemberId(), payWaterEntity.getUserType(), payWaterEntity.getOemCode(), payWaterEntity.getWalletType());
            if (accEntity == null) {
                throw new BusinessException("资金账户不存在");
            }
            userCapitalAccountService.unfreezeBalance(accEntity, orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getPayAmount(), updateUser);
        }
        orderMapper.updateByPrimaryKeySelective(orderEntity);
        mapper.updateByPrimaryKeySelective(payWaterEntity);
    }

    @Override
    @Transactional
    public void updateWaterAndOrderByRecharge(PayWaterEntity payWaterEntity, OrderEntity orderEntity, Integer auditStatus, String payWaterImgs, String auditRemark, String updateUser) {
        Date date = new Date();
        orderEntity.setAuditRemark(auditRemark);
        orderEntity.setUpdateTime(date);
        orderEntity.setUpdateUser(updateUser);
        payWaterEntity.setUpdateTime(date);
        payWaterEntity.setUpdateUser(updateUser);
        if (Objects.equals(auditStatus, 1)) {
            //审核通过
            orderEntity.setPayWaterImgs(payWaterImgs);
            orderEntity.setAuditStatus(AuditStateEnum.APPROVED.getValue());
            orderEntity.setOrderStatus(RACWStatusEnum.PAYED.getValue());
            payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
            payWaterEntity.setPayTime(date);
            //删除冻结款
            UserCapitalAccountEntity accEntity = userCapitalAccountService.queryByUserIdAndType(payWaterEntity.getMemberId(), payWaterEntity.getUserType(), payWaterEntity.getOemCode(), payWaterEntity.getWalletType());
            if (accEntity == null) {
                throw new BusinessException("资金账户不存在");
            }
            orderService.addFreezeBalance(accEntity, orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getPayAmount(),updateUser);
        } else {
            orderEntity.setAuditStatus(AuditStateEnum.APPROVE_NO_PASS.getValue());
            orderEntity.setOrderStatus(RACWStatusEnum.AUDIT_FAIL.getValue());
            // 支付流水状态变成  支付失败
            payWaterEntity.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
           /* //资金解冻
            UserCapitalAccountEntity accEntity = userCapitalAccountService.queryByUserIdAndType(payWaterEntity.getMemberId(), payWaterEntity.getUserType(), payWaterEntity.getOemCode(), payWaterEntity.getWalletType());
            if (accEntity == null) {
                throw new BusinessException("资金账户不存在");
            }
            userCapitalAccountService.unfreezeBalance(accEntity, orderEntity.getOrderNo(), orderEntity.getOrderType(), orderEntity.getPayAmount(), updateUser);*/
        }
        orderMapper.updateByPrimaryKeySelective(orderEntity);
        mapper.updateByPrimaryKeySelective(payWaterEntity);
    }

    @Override
    public List<Map<String, Object>> batchDownIdCard(WthdrawQuery query) {
        return mapper.batchDownIdCard(query);
    }

    @Override
    public List<PayWaterEntity> queryPayWaterListByOrderNoAndStaus(String orderNo) {
        return mapper.queryPayWaterListByOrderNoAndStaus(orderNo);
    }

    @Override
    public void disposeRefundFailed(OrderEntity order, String userAccount, int type) {
        // 修改订单状态为"已取消"
        if (type == 1) { //工单审核失败退款
            order.setOrderStatus(RegOrderStatusEnum.FAILED.getValue());
        } else if (type == 2) { //取消注册订单退款
            order.setOrderStatus(RegOrderStatusEnum.CANCELLED.getValue());
        }
        orderService.editByIdSelective(order);
        // 查询支付流水
        PayWaterEntity payEntity = new PayWaterEntity();
        payEntity.setOemCode(order.getOemCode());
        payEntity.setOrderNo(order.getOrderNo());
        payEntity.setPayWaterType(PayWaterTypeEnum.THIRD.getValue());
        payEntity.setPayStatus(PayWaterStatusEnum.PAY_SUCCESS.getValue());
        payEntity = this.selectOne(payEntity);
        if (null == payEntity) {
            throw new BusinessException("未查询到支付流水");
        }
        // 更新支付流水退款状态为“退款失败”
        payEntity.setRefundStatus(RefundWaterStatusEnum.REFUND_FAILURE.getValue());
        this.editByIdSelective(payEntity);
        // 生成失败的退款流水
        PayWaterEntity refundWater = new PayWaterEntity();
        ObjectUtil.copyObject(payEntity, refundWater);
        refundWater.setId(null);
        refundWater.setPayWaterType(PayWaterTypeEnum.REFUND.getValue());
        refundWater.setPayStatus(PayWaterStatusEnum.PAY_FAILURE.getValue());
        refundWater.setPayNo(UniqueNumGenerator.generatePayNo());
        refundWater.setAddUser(userAccount);
        refundWater.setAddTime(new Date());
        refundWater.setUpdateTime(null);
        refundWater.setUpdateUser(null);
        refundWater.setRefundStatus(null);
        this.insertSelective(refundWater);
    }

    @Override
    public PayWaterEntity getPayWaterByOrderNo(String orderNo) {
        return mapper.getPayWaterByOrderNo(orderNo);
    }
}

