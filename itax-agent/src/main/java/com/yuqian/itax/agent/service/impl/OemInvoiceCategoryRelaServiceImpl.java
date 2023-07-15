package com.yuqian.itax.agent.service.impl;

import com.yuqian.itax.agent.entity.InvoiceInfoByOemEntity;
import com.yuqian.itax.agent.entity.vo.InvoiceCategoryBaseStringAgentVO;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.agent.dao.OemInvoiceCategoryRelaMapper;
import com.yuqian.itax.agent.entity.OemInvoiceCategoryRelaEntity;
import com.yuqian.itax.agent.service.OemInvoiceCategoryRelaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("oemInvoiceCategoryRelaService")
public class OemInvoiceCategoryRelaServiceImpl extends BaseServiceImpl<OemInvoiceCategoryRelaEntity,OemInvoiceCategoryRelaMapper> implements OemInvoiceCategoryRelaService {

    @Override
    public Map queryCategoryNameByOemCode(String oemCode) {
        return mapper.queryCategoryNameByOemCode(oemCode);
    }

    @Override
    public void delByOemCode(String oemCode) {
        mapper.delByOemCode(oemCode);
    }

    @Override
    public void deleteByCategoryBaseId(Long categoryBaseId) {
        mapper.deleteByCategoryBaseId(categoryBaseId);
    }

    @Override
    public void addBatch(InvoiceInfoByOemEntity invoiceInfoByOemEntity, List<InvoiceCategoryBaseStringAgentVO> categoryList) {
        mapper.addBatch( invoiceInfoByOemEntity,  categoryList);
    }

    @Override
    public void batchUpdateByCategoryBaseId(Long categortBaseId) {
        mapper.batchUpdateByCategoryBaseId(categortBaseId);
    }
}

