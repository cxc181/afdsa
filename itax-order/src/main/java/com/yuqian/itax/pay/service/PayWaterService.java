package com.yuqian.itax.pay.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.pay.dao.PayWaterMapper;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.pay.entity.query.PayWaterQuery;
import com.yuqian.itax.pay.entity.query.WthdrawQuery;
import com.yuqian.itax.pay.entity.vo.PaywaterVO;
import com.yuqian.itax.pay.entity.vo.WithdrawVO;

import java.util.List;
import java.util.Map;

/**
 * 支付流水service
 * 
 * @Date: 2019年12月07日 20:13:40 
 * @author 蒋匿
 */
public interface PayWaterService extends IBaseService<PayWaterEntity, PayWaterMapper> {


    List<WithdrawVO> withdrawList(WthdrawQuery wthdrawQuery);

    /**
     * 提现记录列表（分页）
     * @auth hz
     * @date 2019/12/12
     * @param wthdrawQuery
     * @return
     */
    PageInfo<WithdrawVO> withdrawPageInfo(WthdrawQuery wthdrawQuery);
    /**
     * @Description 更新支付流水信息
     * @Author  Kaven
     * @Date   2019/12/12 16:09
     * @Param  orderPay
    */
    void updatePayWater(PayWaterEntity orderPay);

    /**
     * 提现流水分页记录
     * @return
     */
    PageInfo<PayWaterEntity> listPagePayWater(PayWaterQuery query);

    /**
     * 提现流水记录列表
     * @return
     */
    List<PaywaterVO> listPayWater(PayWaterQuery query);

    /**
     * 支付流水分页记录
     * @return
     */
    PageInfo<PaywaterVO> payWaterPageInfo(PayWaterQuery query);

    /**
     * 支付流水list
     * @return
     */
    List<PaywaterVO> payWaterList(PayWaterQuery query);

    /**
     * @Description 查询支付中状态的微信支付订单流水
     * @Author  Kaven
     * @Date   2020/1/16 11:04
     * @Param  payWay 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
     * @Return List
     * @Exception
    */
    List<PayWaterEntity> selectPayingList(Integer payWay);

    /**
     * @Description 查询支付中状态的微信退款订单流水
     * @Author  HZ
     * @Date   2021/8/18 11:04
     * @Param  payWay 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付
     * @param orderBy 排序方式 1-正序 2-倒序
     * @Return List
     * @Exception
     */
    List<PayWaterEntity> selectRefundPayingList(Integer payWay, Integer orderBy);

    /**
     * @Description 根据订单号更新流水状态
     * @Author  Kaven
     * @Date   2020/3/5 9:30
     * @Param  water
     * @Return
     * @Exception
    */
    void updatePayStatus(PayWaterEntity water);

    /**
     * @Description 根据流水号更新流水状态
     * @Author  Kaven
     * @Date   2020/3/17 11:10
     * @Param  payNo-流水号
     * @Param  updateUser-更新用户账号
     * @Param  payStatus-支付状态
     * @Param  upStatusCode-上游返回码
     * @Param  upResultMsg-上游返回信息
     * @Return
     * @Exception
    */
    void updatePayStatusByPayNo(String payNo,String updateUser, Integer payStatus,String upStatusCode,String upResultMsg);

    /**
     * 修改流水状态和订单状态
     * @param payWaterEntity
     * @param orderEntity
     * @param auditStatus
     * @param payWaterImgs
     * @param auditRemark
     * @param updateUser
     */
    void updateWaterAndOrder(PayWaterEntity payWaterEntity, OrderEntity orderEntity, Integer auditStatus, String payWaterImgs, String auditRemark, String updateUser);

    /**
     * 修改流水状态和订单状态
     * @param payWaterEntity
     * @param orderEntity
     * @param auditStatus
     * @param payWaterImgs
     * @param auditRemark
     * @param updateUser
     */
    void updateWaterAndOrderByRecharge(PayWaterEntity payWaterEntity, OrderEntity orderEntity, Integer auditStatus, String payWaterImgs, String auditRemark, String updateUser);


    /**
     * 批量下载身份信息(默认vip,税务顾问得佣金钱包和税)
     * @param query
     * @return
     */
    List<Map<String,Object>> batchDownIdCard(WthdrawQuery query);

    /**
     * 查询支付流水根据订单号和支付成功状态
     */
    List<PayWaterEntity> queryPayWaterListByOrderNoAndStaus(String orderNo);

    /**
     * 退款失败未拿到返回结果处理
     * @param type 1-工单审核失败退款 2-取消注册订单退款 3-重新退款
     */
    void disposeRefundFailed(OrderEntity order, String userAccount, int type);

    /**
     * 根据订单编号查询订单类型为补税并支付状态为支付成功的流水
     * @param orderNo
     * @return
     */
    PayWaterEntity getPayWaterByOrderNo(String orderNo);
}

