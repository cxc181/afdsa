package com.yuqian.itax.user.service.impl;

import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.entity.InvoiceCategoryEntity;
import com.yuqian.itax.system.service.InvoiceCategoryService;
import com.yuqian.itax.user.dao.CompanyInvoiceCategoryMapper;
import com.yuqian.itax.user.entity.CompanyInvoiceCategoryEntity;
import com.yuqian.itax.user.entity.vo.CompanyInvoiceCategoryJdVO;
import com.yuqian.itax.user.service.CompanyInvoiceCategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service("companyInvoiceCategoryService")
public class CompanyInvoiceCategoryServiceImpl extends BaseServiceImpl<CompanyInvoiceCategoryEntity,CompanyInvoiceCategoryMapper> implements CompanyInvoiceCategoryService {

    @Autowired
    private InvoiceCategoryService invoiceCategoryService;

    @Override
    public void updateCompanyIdByOrderNo(Long companyId, String orderNo, String oemCode) {
        mapper.updateCompanyIdByOrderNo(companyId, orderNo, oemCode);
    }

    @Override
    public void addBatch(CompanyInvoiceCategoryEntity entity, List<String> categoryNames) {
        mapper.addBatch(entity, categoryNames);
    }

    @Override
    public List<String> getCategoryNames(String orderNo, String oemCode, Long industryId) {
        //查询企业开票类目
        if (StringUtils.isNotBlank(orderNo)) {
            CompanyInvoiceCategoryEntity entity = new CompanyInvoiceCategoryEntity();
            entity.setOrderNo(orderNo);
            entity.setOemCode(oemCode);
            entity.setIndustryId(industryId);
            List<CompanyInvoiceCategoryEntity> companyInvoiceCategoryEntities = mapper.select(entity);
            if (CollectionUtil.isNotEmpty(companyInvoiceCategoryEntities)) {
                return companyInvoiceCategoryEntities.stream().map(CompanyInvoiceCategoryEntity::getCategoryName).collect(Collectors.toList());
            }
        }
        //根据行业id获取企业开票类目
        InvoiceCategoryEntity t = new InvoiceCategoryEntity();
        t.setIndustryId(industryId);
        List<InvoiceCategoryEntity> invoiceCategoryEntities = invoiceCategoryService.select(t);
        if (CollectionUtil.isNotEmpty(invoiceCategoryEntities)) {
            return invoiceCategoryEntities.stream().map(InvoiceCategoryEntity::getCategoryName).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 根据行业id保存企业开票类目
     * @param oemCode
     * @param orderNo
     * @param industryId
     * @param addUser
     */
    public void addByIndustryId(String oemCode,String orderNo,Long industryId,String addUser){
        mapper.addByIndustryId(oemCode,orderNo,industryId,addUser);
    }

    @Override
    public void deleteByCompanyId(Long companyId) {
        mapper.deleteByCompanyId(companyId);
    }

    @Override
    public void deleteByCategoryBaseId(Long baseId) {
        mapper.deleteByCategoryBaseId(baseId);
    }

    @Override
    public CompanyInvoiceCategoryEntity queryOemInvoiceCategory(Long companyId) {
        return mapper.queryOemInvoiceCategory(companyId);
    }
    @Override
    public List<CompanyInvoiceCategoryJdVO> queryCompanyInvoiceCategoryJd(Long companyId) {
        return mapper.queryCompanyInvoiceCategoryJd(companyId);
    }

    @Override
    public void batchUpdateByCategoryBaseId(Long categortBaseId) {
        mapper.batchUpdateByCategoryBaseId(categortBaseId);
    }
}

