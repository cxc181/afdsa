package com.yuqian.itax.group.dao;

import com.yuqian.itax.group.entity.InvoiceCategoryGroupEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.group.entity.query.InvoiceHeadGroupQuery;
import com.yuqian.itax.system.entity.query.InvoiceCategoryGroupQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 集团类目表dao
 * 
 * @Date: 2020年03月04日 09:25:37 
 * @author 蒋匿
 */
@Mapper
public interface InvoiceCategoryGroupMapper extends BaseMapper<InvoiceCategoryGroupEntity> {

    /**
     * 获取开票类目
     * @param query
     * @return
     */
    List<InvoiceCategoryGroupEntity> getInvoiceCategoryGroup(InvoiceCategoryGroupQuery query);
}

