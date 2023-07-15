package com.yuqian.itax.group.service;

import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.group.entity.InvoiceCategoryGroupEntity;
import com.yuqian.itax.group.dao.InvoiceCategoryGroupMapper;
import com.yuqian.itax.system.entity.query.InvoiceCategoryGroupQuery;

import java.util.List;

/**
 * 集团类目表service
 * 
 * @Date: 2020年03月04日 09:25:37 
 * @author 蒋匿
 */
public interface InvoiceCategoryGroupService extends IBaseService<InvoiceCategoryGroupEntity,InvoiceCategoryGroupMapper> {
    /**
     * 获取集团类目
     */
    List<InvoiceCategoryGroupEntity> getInvoiceCategoryGroup(InvoiceCategoryGroupQuery query);
}

