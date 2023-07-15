package com.yuqian.itax.product.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.product.dao.ProductDiscountActivityMapper;
import com.yuqian.itax.product.entity.ProductDiscountActivityEntity;
import com.yuqian.itax.product.entity.dto.ProductDiscountActivityAPIDTO;
import com.yuqian.itax.product.entity.query.ProductDiscountActivityQuery;
import com.yuqian.itax.product.entity.vo.*;

import java.util.Date;
import java.util.List;

/**
 * 产品特价活动表service
 * 
 * @Date: 2021年07月15日 15:46:44 
 * @author 蒋匿
 */
public interface ProductDiscountActivityService extends IBaseService<ProductDiscountActivityEntity,ProductDiscountActivityMapper> {

    /**
     * 根据产品类型获取产品特价活动
     * @param productDiscountActivityAPIDTO
     * @return
     */
    ProductDiscountActivityVO getProductDiscountActivityByProductType(ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO);

    PageInfo<ProductDiscountActivityListVO> listPageProductDiscountActivity(ProductDiscountActivityQuery query);

    List<ProductDiscountActivityListVO> listProductDiscountActivity(ProductDiscountActivityQuery query);

    void updateStatusById(Long id, Integer status,String account);
    /**
     * 根据活动名称和活动产品查询
     * @param activityName
     * @param productType
     * @return
     */
    ProductDiscountActivityListVO queryProductDiscountActivityByactivityNameAndType(String activityName,Integer productType,Long activityId);

    /**
     * 根据产品类型和oemCode查询人群标签信息
     * @param oemCode
     * @param productType
     * @return
     */
    List<ProductDiscountActivityOnCrowdLabelVO> getCrowdLabelAndParkByProductType(String oemCode,Integer productType);

    void addProductDiscountActivity(ProductCrowdParkVO productCrowdParkVO,String addUser);

    /**
     * 详情
     * @param discountActivityId
     * @return
     */
    ProductActivityDetailVO getDetail(Long discountActivityId);

    /**
     * 根据人群标签及产品类型查询特价活动
     */
    ProductDiscountActivityEntity queryByCrowdLabel(Long crowdLabelId, Integer productType);

    /**
     * 根据接入方编码查询特价活动
     * @param accessPartyCode
     * @param productType
     * @return
     */
    ProductDiscountActivityEntity getByAccessPartyCode(String accessPartyCode, Integer productType, Long parkId);
}

