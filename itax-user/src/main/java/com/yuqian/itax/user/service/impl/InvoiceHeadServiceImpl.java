package com.yuqian.itax.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.yuqian.itax.common.base.entity.query.BaseQuery;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.user.dao.InvoiceHeadMapper;
import com.yuqian.itax.user.entity.InvoiceHeadEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.dto.EditInvoiceHeadDTO;
import com.yuqian.itax.user.entity.dto.InvoiceHeadDTO;
import com.yuqian.itax.user.enums.InvoiceHeadStatusEnum;
import com.yuqian.itax.user.service.InvoiceHeadService;
import com.yuqian.itax.user.service.MemberAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 发票抬头service impl
 *
 * @Date: 2019年12月08日 20:48:40
 * @author yejian
 */
@Service("invoiceHeadService")
public class InvoiceHeadServiceImpl extends BaseServiceImpl<InvoiceHeadEntity,InvoiceHeadMapper> implements InvoiceHeadService {

    @Resource
    private InvoiceHeadMapper invoiceHeadMapper;
    @Autowired
    private InvoiceHeadService invoiceHeadService;
    @Autowired
    private MemberAccountService memberAccountService;

    @Override
    public List<InvoiceHeadEntity> listInvoiceHead(Long memberId, BaseQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return invoiceHeadMapper.listInvoiceHead(memberId);
    }

    @Override
    @Transactional
    public void insertInvoiceHead(Long memberId, InvoiceHeadDTO entity) throws BusinessException {
        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if(null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        InvoiceHeadEntity head = new InvoiceHeadEntity();
        head.setMemberId(memberId);
        head.setCompanyName(entity.getCompanyName());
        head.setStatus(InvoiceHeadStatusEnum.AVAILABLE.getValue());
        List<InvoiceHeadEntity> headList = invoiceHeadService.select(head);
        if (CollectionUtil.isNotEmpty(headList)) {
            throw new BusinessException("公司名称已存在！");
        }

        head = new InvoiceHeadEntity();
        head.setMemberId(memberId);
        head.setEin(entity.getEin());
        head.setStatus(InvoiceHeadStatusEnum.AVAILABLE.getValue());
        headList = invoiceHeadService.select(head);
        if (CollectionUtil.isNotEmpty(headList)) {
            throw new BusinessException("税号已存在！");
        }

        InvoiceHeadEntity mainHead = transferInvHeadDto2Entity(entity);
        mainHead.setMemberId(memberId);
        mainHead.setStatus(InvoiceHeadStatusEnum.AVAILABLE.getValue());
        mainHead.setAddTime(new Date());
        mainHead.setAddUser(member.getMemberAccount());
        invoiceHeadService.insertSelective(mainHead);
    }

    @Override
    @Transactional
    public void editInvoiceHead(Long memberId, EditInvoiceHeadDTO entity) throws BusinessException{
        // 查询会员账号
        MemberAccountEntity member = memberAccountService.findById(memberId);
        if(null == member) {
            throw new BusinessException("未查询到会员账号");
        }

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("id", entity.getId());
        params.put("memberId", memberId);
        InvoiceHeadEntity mainHead = invoiceHeadMapper.findByMemberId(params);
        if(null == mainHead){
            throw new BusinessException("未查询到会员的发票抬头");
        }

        InvoiceHeadEntity head = new InvoiceHeadEntity();
        head.setMemberId(memberId);
        head.setCompanyName(entity.getCompanyName());
        head.setStatus(InvoiceHeadStatusEnum.AVAILABLE.getValue());
        InvoiceHeadEntity headResult = invoiceHeadService.selectOne(head);
        if (null != headResult && !Objects.equals(headResult.getId(), entity.getId())) {
            throw new BusinessException("公司名称已存在！");
        }

        head = new InvoiceHeadEntity();
        head.setMemberId(memberId);
        head.setEin(entity.getEin());
        head.setStatus(InvoiceHeadStatusEnum.AVAILABLE.getValue());
        headResult = invoiceHeadService.selectOne(head);
        if (null != headResult && !Objects.equals(headResult.getId(), entity.getId())) {
            throw new BusinessException("税号已存在！");
        }

        mainHead = transferInvHeadDto2Entity(entity);
        mainHead.setUpdateUser(String.valueOf(memberId));
        mainHead.setUpdateTime(new Date());
        invoiceHeadService.editByIdSelective(mainHead);
    }

    @Override
    @Transactional
    public void updateInvHeadStatus(Long id, Long memberId) throws BusinessException{
        if(null == id){
            throw new BusinessException(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("id", id);
        params.put("memberId", memberId);
        InvoiceHeadEntity mainHead = invoiceHeadMapper.findByMemberId(params);
        if(null == mainHead){
            throw new BusinessException("未查询到会员的发票抬头");
        }
        mainHead.setStatus(InvoiceHeadStatusEnum.UNAVAILABLE.getValue());
        mainHead.setUpdateUser(String.valueOf(memberId));
        mainHead.setUpdateTime(new Date());
        invoiceHeadService.editByIdSelective(mainHead);
    }

    @Override
    public InvoiceHeadEntity findByMemberId(Long id, Long memberId) throws BusinessException{
        if(null == id){
            throw new BusinessException(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("id",id);
        params.put("memberId",memberId);
        return invoiceHeadMapper.findByMemberId(params);
    }

    /**
     * 对象实体转换
     * @Author yejian
     * @Date 2019/12/12 10:59
     * @param headDTO
     * @return InvoiceHeadEntity
     */
    private InvoiceHeadEntity transferInvHeadDto2Entity(InvoiceHeadDTO headDTO){
        InvoiceHeadEntity invHead = new InvoiceHeadEntity();
        invHead.setCompanyName(headDTO.getCompanyName());
        invHead.setCompanyAddress(headDTO.getCompanyAddress());
        invHead.setEin(headDTO.getEin());
        invHead.setPhone(headDTO.getPhone());
        invHead.setBankName(headDTO.getBankName());
        invHead.setBankNumber(headDTO.getBankNumber());
        invHead.setRemark(headDTO.getRemark());
        return invHead;
    }

    /**
     * 对象实体转换
     * @Author yejian
     * @Date 2019/12/12 10:59
     * @param headDTO
     * @return InvoiceHeadEntity
     */
    private InvoiceHeadEntity transferInvHeadDto2Entity(EditInvoiceHeadDTO headDTO){
        InvoiceHeadEntity invHead = new InvoiceHeadEntity();
        invHead.setId(headDTO.getId());
        invHead.setCompanyName(headDTO.getCompanyName());
        invHead.setCompanyAddress(headDTO.getCompanyAddress());
        invHead.setEin(headDTO.getEin());
        invHead.setPhone(headDTO.getPhone());
        invHead.setBankName(headDTO.getBankName());
        invHead.setBankNumber(headDTO.getBankNumber());
        invHead.setRemark(headDTO.getRemark());
        return invHead;
    }
}