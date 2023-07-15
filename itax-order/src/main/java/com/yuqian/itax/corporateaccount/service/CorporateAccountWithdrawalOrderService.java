package com.yuqian.itax.corporateaccount.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.corporateaccount.dao.CorporateAccountWithdrawalOrderMapper;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountWithdrawalOrderEntity;
import com.yuqian.itax.corporateaccount.query.CorporateAccountWithdrawOrderQuery;
import com.yuqian.itax.corporateaccount.query.CorporateAccountWithdrawWaterQuery;
import com.yuqian.itax.corporateaccount.vo.CorporateAccountWithdrawOrderVO;
import com.yuqian.itax.corporateaccount.vo.CorporateAccountWithdrawWaterVO;
import com.yuqian.itax.order.entity.dto.CorpAccountWithdrawOrderDTO;
import com.yuqian.itax.order.entity.vo.CorpAccountWithdrawOrderDetailVO;
import com.yuqian.itax.order.entity.vo.CorpAccountWithdrawOrderVO;
import com.yuqian.itax.user.entity.CompanyCorporateAccountEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.query.ComCorpAccQuery;

import java.util.List;

/**
 *  对公户提现订单表service
 *
 * @Date: 2020年09月07日 09:12:30
 * @author 蒋匿
 */
public interface CorporateAccountWithdrawalOrderService extends IBaseService<CorporateAccountWithdrawalOrderEntity,CorporateAccountWithdrawalOrderMapper> {

    /**
     * 分页查询对公户提现订单记录
     * @param query
     * @return
     */
    PageInfo<CorporateAccountWithdrawOrderVO> listPageCorporateWithdrawOrder(CorporateAccountWithdrawOrderQuery query);

    /**
     * 查询对公户提现订单记录
     * @param query
     * @return
     */
    List<CorporateAccountWithdrawOrderVO> listCorporateWithdrawOrder(CorporateAccountWithdrawOrderQuery query);

    /**
     * 分页查询对公户提现流水
     * @param query
     * @return
     */
    PageInfo<CorporateAccountWithdrawWaterVO> listPageCorporateWithdrawWater(CorporateAccountWithdrawWaterQuery query);

    /**
     * 查询对公户提现订单流水
     * @param query
     * @return
     */
    List<CorporateAccountWithdrawWaterVO> listCorporateWithdrawWater(CorporateAccountWithdrawWaterQuery query);

    /**
     * @Description 创建对公户提现订单
     * @Author  Kaven
     * @Date   2020/9/8 16:51
     * @Param   CorpAccountWithdrawOrderDTO
     * @Return  String
     * @Exception  BusinessException
    */
    String createCorpAccountWithdrawOrder(CorpAccountWithdrawOrderDTO dto) throws BusinessException;

    /**
     * @Description 提现订单入库
     * @Author  Kaven
     * @Date   2020/9/9 13:53
     * @Param   CorpAccountWithdrawOrderDTO MemberAccountEntity CompanyCorporateAccountEntity
     * @Return  String
     * @Exception  BusinessException
    */
    String createWithdrawOrder(CorpAccountWithdrawOrderDTO dto, MemberAccountEntity member, CompanyCorporateAccountEntity corporateAccount) throws BusinessException;

    /**
     * @Description 对公户提现订单确认
     * @Author  Kaven
     * @Date   2020/9/8 16:51
     * @Param   orderNo verifyCode oemCode currUserId
     * @Return String
     * @Exception BusinessException
    */
    String confirmWithdrawOrder(String orderNo, String verifyCode, String oemCode, Long currUserId) throws BusinessException;

    /**
     * @Description 查询对公户提现记录
     * @Author  Kaven
     * @Date   2020/9/9 15:22
     * @Param   ComCorpAccQuery
     * @Return  CorpAccountWithdrawOrderVO
     * @Exception  BusinessException
    */
    CorpAccountWithdrawOrderVO listWithdrawOrderPage(ComCorpAccQuery query) throws BusinessException;

    /**
     * @Description 对公户提现订单详情查询
     * @Author  Kaven
     * @Date   2020/9/9 16:23
     * @Param   orderNo
     * @Return  CorpAccountWithdrawOrderDetailVO
     * @Exception  BusinessException
    */
    CorpAccountWithdrawOrderDetailVO getWithdrawOrderDetail(String orderNo) throws BusinessException;

    /**
     * 根据开票订单编号查询提现记录
     * @param invoiceOrderNO
     * @return
     */
    List<CorporateAccountWithdrawalOrderEntity> queryWithdrawOrderByInvoiceOrderNo(String invoiceOrderNO);
}

