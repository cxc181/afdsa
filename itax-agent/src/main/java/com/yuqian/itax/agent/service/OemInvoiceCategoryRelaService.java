package com.yuqian.itax.agent.service;

import com.yuqian.itax.agent.entity.InvoiceInfoByOemEntity;
import com.yuqian.itax.agent.entity.vo.InvoiceCategoryBaseStringAgentVO;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.agent.entity.OemInvoiceCategoryRelaEntity;
import com.yuqian.itax.agent.dao.OemInvoiceCategoryRelaMapper;

import java.util.List;
import java.util.Map;

/**
 * oem机构开票类目关系表service
 * 
 * @Date: 2020年12月25日 11:41:14 
 * @author 蒋匿
 */
public interface OemInvoiceCategoryRelaService extends IBaseService<OemInvoiceCategoryRelaEntity,OemInvoiceCategoryRelaMapper> {

    /**
     * 查询机构开票类目名称集合
     * @param oemCode
     * @return
     */
    Map queryCategoryNameByOemCode(String oemCode);

    void delByOemCode(String oemCode);

    void deleteByCategoryBaseId(Long categoryBaseId);

    void addBatch(InvoiceInfoByOemEntity invoiceInfoByOemEntity,List<InvoiceCategoryBaseStringAgentVO> categoryList);
    /**
     *  根据基础类目id批量更新
     */
    void batchUpdateByCategoryBaseId(Long categortBaseId);
}

