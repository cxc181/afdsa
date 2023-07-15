package com.yuqian.itax.user.service;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.user.dao.InvoiceHeadMapper;
import com.yuqian.itax.user.entity.InvoiceHeadEntity;
import com.yuqian.itax.user.entity.dto.EditInvoiceHeadDTO;
import com.yuqian.itax.user.entity.dto.InvoiceHeadDTO;

import java.util.List;


/**
 * 发票抬头service
 *
 * @Date: 2019年12月07日 20:48:40 
 * @author yejian
 */
public interface InvoiceHeadService extends IBaseService<InvoiceHeadEntity,InvoiceHeadMapper> {

    /**
     * 查询发票抬头列表
     * @return
     */
    List<InvoiceHeadEntity> listInvoiceHead(Long memberId, BaseQuery query);

    /**
     * 添加发票抬头
     * @return
     */
    void insertInvoiceHead(Long memberId, InvoiceHeadDTO entity) throws BusinessException;

    /**
     * 编辑发票抬头
     * @return
     */
    void editInvoiceHead(Long memberId, EditInvoiceHeadDTO entity) throws BusinessException;

    /**
     * 删除发票抬头
     * @return
     */
    void updateInvHeadStatus(Long id, Long memberId) throws BusinessException;

    /**
     * 根据主键id和会员id查询发票抬头
     * @return
     */
    InvoiceHeadEntity findByMemberId(Long id, Long memberId) throws BusinessException;
}
