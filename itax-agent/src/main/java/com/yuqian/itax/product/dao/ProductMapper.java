package com.yuqian.itax.product.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.vo.ProductOemVO;
import com.yuqian.itax.product.entity.query.ProductQuery;
import com.yuqian.itax.product.entity.vo.ProductOfTaxCalculatorVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 产品管理dao
 * 
 * @Date: 2019年12月07日 20:41:26 
 * @author 蒋匿
 */
@Mapper
public interface ProductMapper extends BaseMapper<ProductEntity> {
    /**
     * @Description 查询当前OEM机构下开通的产品列表
     * @Author  Kaven
     * @Date   2019/12/10 11:00
     * @Param oemCode
     * @Return List
    */
    List<ProductEntity> queryProductList(String oemCode);

    /**
     * 根据oemCode和产品类型查询
     * @param oemCode
     * @return
     */
    List<ProductEntity> queryProductListByOemCodeAndType(String oemCode);

    /**
     * 查询产品列表
     * @param query
     * @return
     */
    List<ProductOemVO> listProduct(ProductQuery query);

    /**
     * 修改订单状态
     * @param id
     * @param status 状态 1-上架 2-下架 3-暂停
     * @param updateTime
     */
    void updateStatus(@Param("id")Long id, @Param("status")Integer status,
                      @Param("updateUser")String updateUser, @Param("updateTime")Date updateTime);

    /**
     * 根据产品类型查询未下架产品
     * @param prodType 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-税务顾问 10-城市服务商
     * @param oemCode
     * @param status  状态 0-待上架 1-已上架 2-已下架 3-已暂停
     * @return
     */
    ProductEntity queryProductByProdType(@Param("prodType")Integer prodType, @Param("oemCode")String oemCode,
                                         @Param("status") Integer status, @Param("parkId")Long parkId);

    /**
     * 根据机构园区关系、园区产品关系查询产品列表
     * @param oemCode
     * @param prodType 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户 5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票 9-黄金会员（废弃） 10-钻石会员 （废弃）
     *                 11-个体注销 12-个独注销 13-有限合伙注销 14-有限责任注销 15-公户申请和托管 16-个体托管费续费
     * @return
     */
    ProductEntity queryProductByRelation(@Param("memberId") Long memberId, @Param("oemCode") String oemCode, @Param("prodType") Integer prodType);

    /**
     * 获取税费测算工具产品列表
     * @param oemCode
     * @return
     */
    List<ProductOfTaxCalculatorVO> getTaxCalculatorProductList(String oemCode);
}

