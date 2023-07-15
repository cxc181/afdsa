package com.yuqian.itax.tax.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.order.entity.RegisterOrderEntity;
import com.yuqian.itax.order.service.RegisterOrderService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.tax.dao.CompanyTaxCostItemMapper;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.tax.entity.CompanyTaxCostItemEntity;
import com.yuqian.itax.tax.entity.vo.CompanyTaxCostVo;
import com.yuqian.itax.tax.service.CompanyTaxBillService;
import com.yuqian.itax.tax.service.CompanyTaxCostItemService;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.service.MemberCompanyService;
import com.yuqian.itax.util.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("companyTaxCostItemService")
public class CompanyTaxCostItemServiceImpl extends BaseServiceImpl<CompanyTaxCostItemEntity,CompanyTaxCostItemMapper> implements CompanyTaxCostItemService {

    @Autowired
    private CompanyTaxBillService companyTaxBillService;

    @Autowired
    private MemberCompanyService memberCompanyService;

    @Autowired
    private RegisterOrderService registerOrderService;
    @Autowired
    private OssService ossService;

    /**
     * 根据企业税单id获取成本明细数据
     * @param companyTaxId
     * @return
     */
    public CompanyTaxCostVo getCoseItemsByCompanyTaxId(Long companyTaxId){
        CompanyTaxBillEntity companyTaxBillEntity=companyTaxBillService.findById(companyTaxId);
        if(companyTaxBillEntity==null){
            throw  new BusinessException("企业税单不存在");
        }
        MemberCompanyEntity memberCompanyEntity=memberCompanyService.findById(companyTaxBillEntity.getCompanyId());
        if(memberCompanyEntity==null){
            throw  new BusinessException("没找到企业数据");
        }
        CompanyTaxCostVo  vo = new CompanyTaxCostVo(companyTaxBillEntity,memberCompanyEntity);
        if(StringUtil.isNotBlank(memberCompanyEntity.getOrderNo())){
            RegisterOrderEntity registerOrderEntity = registerOrderService.queryByOrderNo(memberCompanyEntity.getOrderNo());
            if(registerOrderEntity!=null){
                vo.setSignImg(ossService.getPrivateImgUrl(registerOrderEntity.getSignImg()));
            }
        }
        if (StringUtil.isNotBlank(vo.getCostItemImgs())){
            List<String> list = getOssImages(vo.getCostItemImgs());
            vo.setCostItemImgsList(list);
        }
        CompanyTaxCostItemEntity itemEntity = new CompanyTaxCostItemEntity();
        itemEntity.setCompanyTaxId(companyTaxId);
        List<CompanyTaxCostItemEntity> list = this.select(itemEntity);
        if(list!=null && list.size()>0){
            vo.setCostItemList(list);
        }
        return vo;
    }

    /**
     * 获取oss上图片集合
     * @param url
     * @return
     */
    private List<String> getOssImages(String url) {
        List<String> list = Lists.newArrayList();
        if (StringUtils.isBlank(url)) {
            return list;
        }
        //oss获取银行流水
        JSONArray split = JSONObject.parseArray(url);
        for (Object s : split) {
            list.add(ossService.getPrivateVideoUrl(s.toString()));
        }
        return list;
    }
    @Override
    public void insertAll(List<CompanyTaxCostItemEntity> costs) {
        mapper.insertList(costs);
    }

    @Override
    public void deleteByCompanyTaxId(Long companyTaxId) {
        mapper.deleteByCompanyTaxId(companyTaxId);
    }
}

