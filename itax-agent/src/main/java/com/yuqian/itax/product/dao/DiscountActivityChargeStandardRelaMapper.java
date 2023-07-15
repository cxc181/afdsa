package com.yuqian.itax.product.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.product.entity.DiscountActivityChargeStandardRelaEntity;
import com.yuqian.itax.product.entity.vo.DiscountActivityChangeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 特价活动开票服务费标准dao
 * 
 * @Date: 2021年07月15日 15:47:32 
 * @author 蒋匿
 */
@Mapper
public interface DiscountActivityChargeStandardRelaMapper extends BaseMapper<DiscountActivityChargeStandardRelaEntity> {

    /**
     * 获取特价活动开票服务费阶梯
     * @param memberId
     * @param parkId
     * @param industryId
     * @param productType
     * @param oemCode
     * @return
     */
    List<DiscountActivityChargeStandardRelaEntity> discountActivityInvoiceChargeStandard(@Param(value = "memberId") Long memberId,@Param(value = "parkId") Long parkId,
                                                                                         @Param(value = "industryId") Long industryId,@Param(value = "productType") Integer productType,
                                                                                         @Param(value = "oemCode")String oemCode);

    /**
     * 添加数据
     * @param list
     * @param oemCode
     * @param discountActivityId
     */
    void addChargeStandard(List<DiscountActivityChangeVO> list, String oemCode, Long discountActivityId, Integer chargeType, Date addDate,String addUser);

    /**
     * 根据id查询
     * @param list
     * @return
     */
    List<DiscountActivityChargeStandardRelaEntity> queryChargeStandarById(List<String> list);

    /**
     * 根据活动id 删除
     * @param discountActivityId
     */
    void deleteBydiscountActivityId(Long discountActivityId);
	
}

