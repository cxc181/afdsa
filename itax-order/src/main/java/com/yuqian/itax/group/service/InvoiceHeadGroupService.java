package com.yuqian.itax.group.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.group.entity.InvoiceHeadGroupEntity;
import com.yuqian.itax.group.dao.InvoiceHeadGroupMapper;
import com.yuqian.itax.group.entity.po.InvoiceHeadGroupPO;
import com.yuqian.itax.group.entity.query.InvoiceHeadGroupQuery;

import java.util.List;

/**
 * 集团发票抬头service
 * 
 * @Date: 2020年03月04日 09:26:10 
 * @author 蒋匿
 */
public interface InvoiceHeadGroupService extends IBaseService<InvoiceHeadGroupEntity,InvoiceHeadGroupMapper> {

    /**
     *根据机构编码获取集团对应的开票抬头列表
     */
    List<InvoiceHeadGroupEntity> getInvoiceHeadGroup(InvoiceHeadGroupQuery query);

    /**
     *新增集团对应的开票抬头
     */
    InvoiceHeadGroupEntity addInvoiceHeadGroup(InvoiceHeadGroupPO po,String account);

    /**
     * 编辑集团得开票抬头
     */
    InvoiceHeadGroupEntity updateInvoiceHeadGroup(InvoiceHeadGroupPO po,String account);
}

