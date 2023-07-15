package com.yuqian.itax.product.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.product.entity.ProductDiscountActivityEntity;
import com.yuqian.itax.product.entity.dto.ProductDiscountActivityAPIDTO;
import com.yuqian.itax.product.entity.query.ProductDiscountActivityQuery;
import com.yuqian.itax.product.entity.vo.ProductActivityDetailVO;
import com.yuqian.itax.product.entity.vo.ProductDiscountActivityListVO;
import com.yuqian.itax.product.entity.vo.ProductDiscountActivityOnCrowdLabelVO;
import com.yuqian.itax.product.entity.vo.ProductDiscountActivityVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品特价活动表dao
 * 
 * @Date: 2021年07月15日 15:46:44 
 * @author 蒋匿
 */
@Mapper
public interface ProductDiscountActivityMapper extends BaseMapper<ProductDiscountActivityEntity> {

    /**
     * 根据会员和产品类型获取特价获取信息
     * @param productDiscountActivityAPIDTO
     * @return
     */
    ProductDiscountActivityVO getProductDiscountActivityByProductType(ProductDiscountActivityAPIDTO productDiscountActivityAPIDTO);

    /**
     * 分页查询产品列表
     * @param query
     * @return
     */
    List<ProductDiscountActivityListVO> listPageProductDiscountActivity(ProductDiscountActivityQuery query);

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
    List<ProductDiscountActivityOnCrowdLabelVO> getCrowdLabelAndParkByProductType(String oemCode, Integer productType);

    /**
     * 详情
     * @param discountActivityId
     * @return
     */
    ProductActivityDetailVO getDetail(Long discountActivityId);

    /**
     * 根据人群标签及产品类型查询特价活动
     * @param crowdLabelId
     * @param productType
     * @return
     */
    ProductDiscountActivityEntity queryByCrowdLabel(@Param("crowdLabelId") Long crowdLabelId, @Param("productType") Integer productType);

    /**
     * 根据接入方编码查询特价活动
     * @param accessPartyCode
     * @param productType
     * @return
     */
    ProductDiscountActivityEntity queryByAccessPartyCode(@Param("accessPartyCode") String accessPartyCode, @Param("productType") Integer productType, @Param("parkId") Long parkId);
}

