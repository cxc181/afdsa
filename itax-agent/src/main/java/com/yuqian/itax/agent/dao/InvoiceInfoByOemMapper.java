package com.yuqian.itax.agent.dao;

import com.yuqian.itax.agent.entity.InvoiceInfoByOemEntity;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * oem机构开票信息dao
 * 
 * @Date: 2020年06月23日 09:29:36 
 * @author 蒋匿
 */
@Mapper
public interface InvoiceInfoByOemMapper extends BaseMapper<InvoiceInfoByOemEntity> {

    InvoiceInfoByOemEntity queryInvoiceInfoByOemEntityByOemCode(@Param("oemCode")String oemCode);
}

