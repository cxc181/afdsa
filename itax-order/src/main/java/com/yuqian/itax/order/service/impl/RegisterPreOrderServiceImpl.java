package com.yuqian.itax.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.order.dao.RegisterPreOrderMapper;
import com.yuqian.itax.order.entity.RegisterPreOrderEntity;
import com.yuqian.itax.order.entity.dto.RegisterOrderDTO;
import com.yuqian.itax.order.entity.dto.RegisterPreOrderDTO;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.order.service.RegisterPreOrderService;
import com.yuqian.itax.user.entity.CompanyCorePersonnelEntity;
import com.yuqian.itax.user.enums.CompanyCorePersonnelTypeEnum;
import com.yuqian.itax.user.service.CompanyCorePersonnelService;
import com.yuqian.itax.util.util.OrderNoFactory;
import com.yuqian.itax.util.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


@Service("registerPreOrderService")
public class RegisterPreOrderServiceImpl extends BaseServiceImpl<RegisterPreOrderEntity,RegisterPreOrderMapper> implements RegisterPreOrderService {

    @Autowired
    private CompanyCorePersonnelService companyCorePersonnelService;
    @Autowired
    private OrderService orderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addOrUpdate(RegisterPreOrderDTO dto) throws BusinessException {
        if (null == dto.getMemberId()) {
            throw new BusinessException("用户id不能为空");
        }
        if (1==dto.getCompanyType()){
            dto.setTaxpayerType(1);
        }else if(dto.getCompanyType()>1){
            if (null == dto.getTaxpayerType()|| 0 == dto.getTaxpayerType()) {
                throw new BusinessException("纳税人类型不能为空");
            }
        }
        // 开户预订单逻辑校验
        RegisterOrderDTO registerOrderDTO = new RegisterOrderDTO();
        BeanUtil.copyProperties(dto, registerOrderDTO);
        orderService.registerOrderPreHandler(registerOrderDTO, dto.getMemberId(), 1);

        // 预订单编号
        String preOrderNo = "";
        // 预订单
        RegisterPreOrderEntity entity = new RegisterPreOrderEntity();
        // 财务信息
        CompanyCorePersonnelEntity financeInfo = new CompanyCorePersonnelEntity();
        // 监事信息
        CompanyCorePersonnelEntity supervisorInfo = new CompanyCorePersonnelEntity();

        // 查询预订单是否存在，不存在则新增，存在则修改
        RegisterPreOrderEntity preOrder = this.queryByMemberId(dto.getMemberId());
        if (null != preOrder) {
            entity = preOrder;
            preOrderNo = entity.getOrderNo();
        }

        // 校验财务与监事是否为同一人
        if (null != dto.getFinanceInfo() && null != dto.getSupervisorInfo()
                && StringUtil.isNotBlank(dto.getFinanceInfo().getCertificateNo()) && StringUtil.isNotBlank(dto.getSupervisorInfo().getCertificateNo())
                && dto.getFinanceInfo().getCertificateNo().equals(dto.getSupervisorInfo().getCertificateNo())) {
            throw new BusinessException("财务和监事不能相同");
        }

        // 查询财务、监事信息
        boolean isAddFinance = true;
        boolean isAddSupervisor = true;
        if (StringUtil.isNotBlank(preOrderNo)) {
            // 查询财务信息
            Example financeExm = new Example(CompanyCorePersonnelEntity.class);
            financeExm.createCriteria().andEqualTo("memberId", dto.getMemberId())
                    .andEqualTo("orderNo", preOrderNo)
                    .andEqualTo("personnelType", CompanyCorePersonnelTypeEnum.FINANCE.getValue());
            List<CompanyCorePersonnelEntity> finances = companyCorePersonnelService.selectByExample(financeExm);
            if (CollectionUtil.isNotEmpty(finances)) {
                financeInfo = finances.get(0);
                isAddFinance = false;
            }

            // 查询监事信息
            Example supervisorExm = new Example(CompanyCorePersonnelEntity.class);
            financeExm.createCriteria().andEqualTo("memberId", dto.getMemberId())
                    .andEqualTo("orderNo", preOrderNo)
                    .andEqualTo("personnelType", CompanyCorePersonnelTypeEnum.SUPERVISOR.getValue());
            List<CompanyCorePersonnelEntity> supervisors = companyCorePersonnelService.selectByExample(supervisorExm);
            if (CollectionUtil.isNotEmpty(finances)) {
                supervisorInfo = supervisors.get(0);
                isAddSupervisor = false;
            }
        }

        // 预订单信息
        BeanUtils.copyProperties(dto, entity);
        entity.setAddTime(new Date());
        entity.setAddUser(dto.getAddUser());

        // 新增预订单
        if (StringUtil.isBlank(preOrderNo)) {
            preOrderNo = OrderNoFactory.getOrderCode(dto.getMemberId());
            entity.setOrderNo(preOrderNo);
            mapper.insertSelective(entity);
        } else { // 编辑预订单
            mapper.updateByPrimaryKeySelective(entity);
        }

        // 新增/编辑财务信息
        if (null != dto.getFinanceInfo() && StringUtil.isNotBlank(dto.getFinanceInfo().getCertificateNo())) {
            BeanUtils.copyProperties(dto.getFinanceInfo(), financeInfo);
            financeInfo.setAddTime(new Date());
            financeInfo.setAddUser(dto.getAddUser());
            if (isAddFinance) { // 新增
                // 新增财务信息
                financeInfo.setOrderNo(preOrderNo);
                financeInfo.setMemberId(dto.getMemberId());
                financeInfo.setPersonnelType(CompanyCorePersonnelTypeEnum.FINANCE.getValue().toString());
                companyCorePersonnelService.insertSelective(financeInfo);
            } else { // 编辑
                companyCorePersonnelService.editByIdSelective(financeInfo);
            }
        }

        // 新增/编辑监事信息
        if (null != dto.getSupervisorInfo() && StringUtil.isNotBlank(dto.getSupervisorInfo().getCertificateNo())) {
            BeanUtils.copyProperties(dto.getSupervisorInfo(), supervisorInfo);
            supervisorInfo.setAddTime(new Date());
            supervisorInfo.setAddUser(dto.getAddUser());
            if (isAddSupervisor) { // 新增
                // 新增监事信息
                supervisorInfo.setOrderNo(preOrderNo);
                supervisorInfo.setMemberId(dto.getMemberId());
                supervisorInfo.setPersonnelType(CompanyCorePersonnelTypeEnum.SUPERVISOR.getValue().toString());
                companyCorePersonnelService.insertSelective(supervisorInfo);
            } else { // 编辑
                companyCorePersonnelService.editByIdSelective(supervisorInfo);
            }
        }

        return preOrderNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePreOrder(Long memberId) {
        // 查询用户预订单
        RegisterPreOrderEntity entity = this.queryByMemberId(memberId);
        if (null == entity) {
            return;
        }

        // 删除预订单
        mapper.delete(entity);
        // 删除企业核心成员
        Example personnelExm = new Example(CompanyCorePersonnelEntity.class);
        personnelExm.createCriteria().andEqualTo("memberId", memberId).andEqualTo("orderNo", entity.getOrderNo());
        companyCorePersonnelService.delByExample(personnelExm);
    }

    @Override
    public RegisterPreOrderEntity queryByMemberId(Long memberId) {
        Example example = new Example(RegisterPreOrderEntity.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        List<RegisterPreOrderEntity> list = mapper.selectByExample(example);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        if (list.size() > 1) {
            throw new BusinessException("数据错误，用户存在多条注册预订单");
        }
        return list.get(0);
    }
}

