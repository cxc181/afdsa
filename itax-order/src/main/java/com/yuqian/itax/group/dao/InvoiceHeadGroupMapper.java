package com.yuqian.itax.group.dao;

import com.yuqian.itax.group.entity.InvoiceHeadGroupEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.group.entity.query.InvoiceHeadGroupQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 集团发票抬头dao
 * 
 * @Date: 2020年03月04日 09:26:10 
 * @author 蒋匿
 */
@Mapper
public interface InvoiceHeadGroupMapper extends BaseMapper<InvoiceHeadGroupEntity> {

    List<InvoiceHeadGroupEntity> getInvoiceHeadGroup(InvoiceHeadGroupQuery query);
}

