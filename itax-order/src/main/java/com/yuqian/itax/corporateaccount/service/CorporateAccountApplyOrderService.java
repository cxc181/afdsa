package com.yuqian.itax.corporateaccount.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.corporateaccount.dao.CorporateAccountApplyOrderMapper;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountApplyOrderEntity;
import com.yuqian.itax.corporateaccount.entity.vo.CorporateAccountApplyOrderVO;
import com.yuqian.itax.corporateaccount.query.CorporateAccountApplyOrderQuery;
import com.yuqian.itax.corporateaccount.vo.CorporateAccountApplyOrdeVO;
import com.yuqian.itax.user.entity.vo.CompanyCorpAccApplyIndividualVO;

import java.util.List;
import java.util.Map;

/**
 * 对公户申请订单service
 * 
 * @Date: 2020年09月07日 09:11:04 
 * @author 蒋匿
 */
public interface CorporateAccountApplyOrderService extends IBaseService<CorporateAccountApplyOrderEntity, CorporateAccountApplyOrderMapper> {
    PageInfo<CorporateAccountApplyOrdeVO> queryCorporateAccountApplyOrderPageInfo(CorporateAccountApplyOrderQuery query);

    List<CorporateAccountApplyOrdeVO> queryCorporateAccountApplyOrderList(CorporateAccountApplyOrderQuery query);

    /**
     * 订单取消
     */
    void cannel(Long id, String account);

    /**
     * 对公户申请-分页查询申请个体列表
     *
     * @return List<CompanyCorpAccApplyIndividualVO>
     * @Author yejian
     * @Date 2020/9/8 15:28
     * @Param memberId
     * @Param oemCode
     */
    List<CompanyCorpAccApplyIndividualVO> applyIndividuallist(Long memberId, String oemCode) throws BusinessException;

    /**
     * 对公户申请-创建对公户申请订单
     *
     * @param sourceType 操作小程序来源 1-微信小程序 2-支付宝小程序
     * @return Map<String, Object>
     * @Author yejian
     * @Date 2020/9/09 15:28
     * @Param memberId
     * @Param oemCode
     * @Param companyId 企业ID
     */
    Map<String, Object> createApplyOrder(Long memberId, String oemCode, Long companyId, String sourceType) throws BusinessException;

    /**
     * 对公户申请-分页查询申请订单列表
     *
     * @return List<CorporateAccountApplyOrderVO>
     * @Author yejian
     * @Date 2020/9/8 15:28
     * @Param memberId
     * @Param oemCode
     */
    List<CorporateAccountApplyOrderVO> applyOrderList(Long memberId, String oemCode) throws BusinessException;

    /**
     * 对公户申请-取消申请订单
     *
     * @Author yejian
     * @Date 2020/9/8 15:28
     * @Param memberId
     * @Param oemCode
     * @Param orderNo
     */
    void cancelApplyOrder(Long memberId, String oemCode, String orderNo) throws BusinessException;

    /**
     * @Description 根据订单号查询记录
     * @Author yejian
     * @Date 2020/09/09 16:38
     * @Param orderNo
     * @Return CorporateAccountApplyOrderEntity
     */
    CorporateAccountApplyOrderEntity queryByOrderNo(String orderNo);

    /**
     * 根据企业id查询是否对公户申请订单数据
     * @param memberId
     * @param oemCode
     * @param companyId
     * @return
     */
    int queryCorAccApplyingOrder(Long memberId,String oemCode,Long companyId);
}

