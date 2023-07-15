package com.yuqian.itax.coupons.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.coupons.dao.CouponsIssueRecordMapper;
import com.yuqian.itax.coupons.entity.CouponsIssueRecordEntity;
import com.yuqian.itax.coupons.entity.query.CouponsIssueRecordQueryAdmin;
import com.yuqian.itax.coupons.entity.query.CouponsQuery;
import com.yuqian.itax.coupons.entity.vo.CouponsIssueVO;
import com.yuqian.itax.coupons.entity.vo.CouponsIssueVOAdmin;
import com.yuqian.itax.coupons.service.CouponsIssueRecordService;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.enums.ProductTypeEnum;
import com.yuqian.itax.product.service.ProductService;
import com.yuqian.itax.user.enums.MemberCompanyTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service("couponsIssueRecordService")
public class CouponsIssueRecordServiceImpl extends BaseServiceImpl<CouponsIssueRecordEntity,CouponsIssueRecordMapper> implements CouponsIssueRecordService {

    @Autowired
    private ProductService productService;

    @Override
    public PageInfo<CouponsIssueVO> listByMemberId(Long memberId, String oemCode, CouponsQuery query) {
        if (query.getType().equals(1) && StringUtils.isBlank(query.getUsableRange())) {
            throw new BusinessException("可用范围不能为空");
        }
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<CouponsIssueVO> list = mapper.listByMemberId(memberId, oemCode, query.getUsableRange(), query.getType());
            for (int i = 0; i < list.size(); i++) {
                if (query.getType().equals(1) || (query.getType().equals(2) && list.get(i).getStartDate().before(new Date()))) {
                    list.get(i).setUsable(1);
                } else {
                    list.get(i).setUsable(0);
                }
                // 查询产品
                ProductEntity productEntity = Optional.ofNullable(productService.queryProductByProdType(Integer.valueOf(list.get(i).getUsableRange()), oemCode, null)).orElseThrow(() -> new BusinessException("优惠券查询失败，未查询到对应产品"));
                list.get(i).setProductId(productEntity.getId());
                list.get(i).setCompType(productEntity.getCompanyType());
            }
        return new PageInfo(list);
    }

    private List<Integer> transform(String usableRange) {
        String[] usableRanges = usableRange.split(",");
        if (usableRanges.length == 0) {
            throw new BusinessException("可用范围不能为空");
        }
        ArrayList<Integer> list = Lists.newArrayList();
        for (String range : usableRanges) {
            switch (range) {
                case "1":
                    list.add(1);
                    break;
                // 暂定开票为个体户开票
                case "2":
                    list.add(5);
                    break;
                // 暂定注销为个体户注销
                case "3":
                    list.add(11);
                    break;
                case "4":
                    list.add(16);
                    break;
            }
        }
        return list;
    }

    @Override
    public Integer countUsable(Long memberId, String oemCode, String usableRange) {
        return this.mapper.countUsable(memberId, oemCode, usableRange);
    }

    @Override
    public PageInfo<CouponsIssueVOAdmin> queryCouponIssueRecordPageInfo(CouponsIssueRecordQueryAdmin query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        return new PageInfo<>(mapper.queryCouponIssueRecordList(query));
    }

    @Override
    public List<CouponsIssueVOAdmin> queryCouponIssueRecordList(CouponsIssueRecordQueryAdmin query) {
        return mapper.queryCouponIssueRecordList(query);
    }

    @Override
    public List<CouponsIssueRecordEntity> queryOverTimeCouponsIssueRecordEntity() {
        return mapper.queryOverTimeCouponsIssueRecordEntity();
    }

    @Override
    public int getExchangeNumber(Long currUserId, String oemCode, Long couponsId, Long exchangeCodeId) {
        return mapper.queryExchangeNumber(currUserId, oemCode, couponsId, exchangeCodeId);
    }
}

