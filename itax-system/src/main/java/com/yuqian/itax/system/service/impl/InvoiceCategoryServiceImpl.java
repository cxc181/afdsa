package com.yuqian.itax.system.service.impl;

import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.system.dao.InvoiceCategoryMapper;
import com.yuqian.itax.system.entity.InvoiceCategoryEntity;
import com.yuqian.itax.system.entity.dto.IndustryInfoDTO;
import com.yuqian.itax.system.service.InvoiceCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 开票类目service impl
 *
 * @Date: 2019年12月08日 20:37:55
 * @author yejian
 */
@Service("invoiceCategoryService")
public class InvoiceCategoryServiceImpl extends BaseServiceImpl<InvoiceCategoryEntity,InvoiceCategoryMapper> implements InvoiceCategoryService {

    @Resource
    private InvoiceCategoryMapper invoiceCategoryMapper;

    @Override
    public List<InvoiceCategoryEntity> listInvoiceCategory(Long companyId, String oemCode) throws BusinessException {
        if(null == companyId){
            throw new BusinessException(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        return invoiceCategoryMapper.listInvoiceCategory(companyId, oemCode);
    }

    @Override
    public void addBatch(IndustryInfoDTO dto) {
        mapper.addBatch(dto);
    }

    @Override
    public void delByIndustryId(Long industryId) {
        mapper.delByIndustryId(industryId);
    }

    @Override
    public void deleteByCategoryBaseId(Long categoryBaseId) {
        mapper.deleteByCategoryBaseId(categoryBaseId);
    }

    @Override
    public void batchUpdateByCategoryBaseId(Long categortBaseId) {
        mapper.batchUpdateByCategoryBaseId(categortBaseId);
    }

    @Override
    public List<String> findCategoryNameByIndustryId(Long industryId) {
        return mapper.queryCategoryNameByIndustryId(industryId);
    }
}

