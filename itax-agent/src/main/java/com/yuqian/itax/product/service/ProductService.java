package com.yuqian.itax.product.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.product.dao.ProductMapper;
import com.yuqian.itax.product.entity.ProductEntity;
import com.yuqian.itax.product.entity.dto.ProductDTO;
import com.yuqian.itax.product.entity.query.ProductQuery;
import com.yuqian.itax.product.entity.vo.ProductDetailVO;
import com.yuqian.itax.product.entity.vo.ProductOemVO;
import com.yuqian.itax.product.entity.vo.ProductOfTaxCalculatorVO;

import java.util.List;

/**
 * 产品管理service
 * 
 * @Date: 2019年12月07日 20:41:26 
 * @author 蒋匿
 */
public interface ProductService extends IBaseService<ProductEntity,ProductMapper> {
    /**
     * @Description 查询当前OEM开通的产品列表
     * @Author  Kaven
     * @Date   2019/12/10 10:58
     * @Param  oemCode
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
     * @Description 查询产品详情（园区列表）
     * @Author  Kaven
     * @Date   2019/12/10 11:14
     * @Param  productId
     * @Return ProductDetailVO
    */
    ProductDetailVO queryProductDetail(Long productId) throws BusinessException;

    /**
     * 分页查询产品列表
     * @param query
     * @return
     */
    PageInfo<ProductOemVO> listPageProduct(ProductQuery query);

    /**
     * 修改订单状态
     * @param id
     * @param status 状态 1-上架 2-下架 3-暂停
     * @param updateUser
     */
    void updateStatus(Long id, Integer status, String updateUser);

    /**
     * 根据产品类型查询未下架产品
     * @param prodType 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-税务顾问 10-城市服务商
     * @param oemCode
     * @return
     */
    ProductEntity queryProductByProdType(Integer prodType, String oemCode, Long parkId);

    /**
     * 添加产品
     * @param dto
     */
    void addProduct(ProductDTO dto);

    /**
     * 编辑产品
     * @param dto
     */
    void editProduct(ProductDTO dto);

    /**
     * 删除产品
     * @param id
     */
    void deleteProductById(Long id);

    /**
     * 根据机构园区关系、园区产品关系查询产品列表
     * @param oemCode
     * @param prodType 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户 5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票 9-黄金会员（废弃） 10-钻石会员 （废弃）
     *                 11-个体注销 12-个独注销 13-有限合伙注销 14-有限责任注销 15-公户申请和托管 16-个体托管费续费
     * @return
     */
    ProductEntity queryProductByRelation(Long memberId, String oemCode, Integer prodType);

    /**
     * 根据产品类型获取企业类型
     * @param prodType
     * @return
     */
    Integer getCompanyTypeByProdType(Integer prodType);

    /**
     * 获取税费测算工具产品列表
     * @param oemCode
     * @return
     */
    List<ProductOfTaxCalculatorVO> getTaxCalculatorProductList(String oemCode);
}

