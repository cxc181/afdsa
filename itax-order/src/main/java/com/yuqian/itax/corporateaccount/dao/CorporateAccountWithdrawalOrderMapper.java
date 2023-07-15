package com.yuqian.itax.corporateaccount.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.corporateaccount.entity.CorporateAccountWithdrawalOrderEntity;
import com.yuqian.itax.corporateaccount.query.CorporateAccountWithdrawOrderQuery;
import com.yuqian.itax.corporateaccount.query.CorporateAccountWithdrawWaterQuery;
import com.yuqian.itax.corporateaccount.vo.CorporateAccountWithdrawOrderVO;
import com.yuqian.itax.corporateaccount.vo.CorporateAccountWithdrawWaterVO;
import com.yuqian.itax.order.entity.vo.CorpAccWithdrawOrderVO;
import com.yuqian.itax.user.entity.query.ComCorpAccQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  对公户提现订单表dao
 * 
 * @Date: 2020年09月07日 09:12:30 
 * @author 蒋匿
 */
@Mapper
public interface CorporateAccountWithdrawalOrderMapper extends BaseMapper<CorporateAccountWithdrawalOrderEntity> {

    /**
     * 查询对公户提现订单记录
     * @param query
     * @return
     */
    List<CorporateAccountWithdrawOrderVO> listCorporateWithdrawOrder(CorporateAccountWithdrawOrderQuery query);

    /**
     * 查询对公户提现订单流水
     * @param query
     * @return
     */
    List<CorporateAccountWithdrawWaterVO> listCorporateWithdrawWater(CorporateAccountWithdrawWaterQuery query);

    /**
     * @Description 查询对公户当天累计提现金额
     * @Author  Kaven
     * @Date   2020/9/9 09:14
     * @Param   corporateAccountId
     * @Return  Long
     * @Exception
    */
    Long countDailyTotalWithdrawAmount(@Param("corporateAccountId") Long corporateAccountId);

    /**
     * @Description 查询对公户提现订单列表
     * @Author  Kaven
     * @Date   2020/9/9 15:49
     * @Param   ComCorpAccQuery
     * @Return  List<CorpAccWithdrawOrderVO>
     * @Exception
    */
    List<CorpAccWithdrawOrderVO> listWithdrawOrder(ComCorpAccQuery query);

    /**
     * @Description 统计总提现金额
     * @Author  Kaven
     * @Date   2020/9/9 15:50
     * @Param   ComCorpAccQuery
     * @Return  Long
     * @Exception
    */
    Long countTotalWithdrawAmount(ComCorpAccQuery query);

    /**
     * 根据开票订单编号查询提现记录
     * @param invoiceOrderNO
     * @return
     */
    List<CorporateAccountWithdrawalOrderEntity> queryWithdrawOrderByInvoiceOrderNo(@Param("invoiceOrderNO") String invoiceOrderNO);
}

