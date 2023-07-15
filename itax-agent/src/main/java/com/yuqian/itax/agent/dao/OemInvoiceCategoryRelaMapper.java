package com.yuqian.itax.agent.dao;

import com.yuqian.itax.agent.entity.InvoiceInfoByOemEntity;
import com.yuqian.itax.agent.entity.OemInvoiceCategoryRelaEntity;
import com.yuqian.itax.agent.entity.vo.InvoiceCategoryBaseStringAgentVO;
import com.yuqian.itax.common.base.dao.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * oem机构开票类目关系表dao
 * 
 * @Date: 2020年12月25日 11:41:14 
 * @author 蒋匿
 */
@Mapper
public interface OemInvoiceCategoryRelaMapper extends BaseMapper<OemInvoiceCategoryRelaEntity> {

    Map queryCategoryNameByOemCode(@Param("oemCode")String oemCode);

    void delByOemCode(@Param("oemCode")String oemCode);

    void deleteByCategoryBaseId(@Param("categoryBaseId")Long categoryBaseId);

    void addBatch(@Param("entity")InvoiceInfoByOemEntity invoiceInfoByOemEntity,@Param("categoryList") List<InvoiceCategoryBaseStringAgentVO> categoryList);

    /**
     * 根据基础类目id批量
     */
    void batchUpdateByCategoryBaseId(@Param("categoryBaseId") Long categortBaseId);
}

