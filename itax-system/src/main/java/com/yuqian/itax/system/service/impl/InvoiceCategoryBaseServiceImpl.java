package com.yuqian.itax.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.system.dao.ClassificationCodeVatMapper;
import com.yuqian.itax.system.dao.InvoiceCategoryBaseMapper;
import com.yuqian.itax.system.entity.InvoiceCategoryBaseEntity;
import com.yuqian.itax.system.entity.dto.InvoiceCategoryBaseDTO;
import com.yuqian.itax.system.entity.query.InvoiceCategoryBaseQuery;
import com.yuqian.itax.system.entity.vo.GroupSelectVO;
import com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseVO;
import com.yuqian.itax.system.entity.vo.SearchInvoiceCategoryBaseVO;
import com.yuqian.itax.system.service.InvoiceCategoryBaseService;
import com.yuqian.itax.util.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


@Service("invoiceCategoryBaseService")
public class InvoiceCategoryBaseServiceImpl extends BaseServiceImpl<InvoiceCategoryBaseEntity,InvoiceCategoryBaseMapper> implements InvoiceCategoryBaseService {

    @Resource
    private ClassificationCodeVatMapper classificationCodeVatMapper;

    @Override
    public PageInfo<InvoiceCategoryBaseVO> queryInvoiceCategoryBasePageInfo(InvoiceCategoryBaseQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        List<InvoiceCategoryBaseVO> list = mapper.queryInvoiceCategoryBaseList(query);
        if(CollectionUtil.isNotEmpty(list)){
             for(InvoiceCategoryBaseVO vo :list){
                 String taxClassificationCode = vo.getTaxClassificationCode();
                 // 根据税收分类编码查询对应的  增值税率
                 String vatFeeRate = classificationCodeVatMapper.queryVatFeeRate(taxClassificationCode);
                 vo.setVatFeeRate(vatFeeRate);
             }
        }
        return new PageInfo<>(list);
    }

    @Override
    public List<InvoiceCategoryBaseVO> queryInvoiceCategoryBaseList(InvoiceCategoryBaseQuery query) {
        List<InvoiceCategoryBaseVO> list = mapper.queryInvoiceCategoryBaseList(query);
        if(CollectionUtil.isNotEmpty(list)){
            for(InvoiceCategoryBaseVO vo :list){
                String taxClassificationCode = vo.getTaxClassificationCode();
                // 根据税收分类编码查询对应的  增值税率
                String vatFeeRate = classificationCodeVatMapper.queryVatFeeRate(taxClassificationCode);
                vo.setVatFeeRate(vatFeeRate);
            }
        }
        return list;
    }

    @Override
    public void add(InvoiceCategoryBaseDTO dto) {
        mapper.batchAdd(dto);
    }

    @Override
    public List<String> queryTaxClassificationAbbreviationList(InvoiceCategoryBaseQuery query) {
        return mapper.queryTaxClassificationAbbreviationList(query);
    }

    @Override
    public List<GroupSelectVO> queryInvoiceCategoryBaseGroupSelect() {
        return mapper.queryInvoiceCategoryBaseGroupSelect();
    }

    @Override
    public List<SearchInvoiceCategoryBaseVO> getCategoryByKeyWord(String keyWord) {
        if (StringUtil.isBlank(keyWord)) {
            return null;
        }
        return mapper.queryCategoryByKeyWord(keyWord);
    }

    /**
     * 根据类目id获取增值税率列表
     * @param categoryId 基础类目id
     * @return
     */
    @Override
    public List<BigDecimal> findVatFeeRateByCategoryId(Long categoryId){
        return mapper.findVatFeeRateByCategoryId(categoryId);
    }
}

