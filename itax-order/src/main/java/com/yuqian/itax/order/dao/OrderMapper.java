package com.yuqian.itax.order.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.query.OrderQuery;
import com.yuqian.itax.order.entity.query.RegOrderQuery;
import com.yuqian.itax.order.entity.query.TZBOrderQuery;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.user.entity.query.MemberExtendQuery;
import com.yuqian.itax.user.entity.vo.ExtendMemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单表dao
 * 
 * @Date: 2019年12月06日 11:34:46 
 * @author 蒋匿
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
    /**
     * @Description 分页查询订单记录
     * @Author  Kaven
     * @Date   2019/12/6 16:40
     * @Param  query
     * @Return List
    */
    List<RegisterOrderVO> listPages(RegOrderQuery query);

    /**
     * 查询用户当天已提现的金额（非取消状态的订单总金额）
     * @param userId
     * @param userType
     * @param statisticType 统计类型 1-按天  2-按月
     * @return
     */
    Long queryCurrentDayWithdrawalAmountByUserId(@Param("userId") Long userId,@Param("userType") Integer userType,@Param("statisticType") Integer statisticType,@Param("withdrawType") Integer withdrawType);
    /**
     * 查询开户订单
     * @param query
     * @return
     */
    List<OpenOrderVO> listOpenOrder(OrderQuery query);

    /**
     * 查询推送订单
     * @param query
     * @return
     */
    List<OpenOrderVO> listPushOrder(OrderQuery query);

    /**
     * 查询开票订单
     * @param query
     * @return
     */
    List<InvOrderVO> listInvOrder(OrderQuery query);
    /**
     * 查询会员升级订单
     * @param query
     * @return
     */
    List<MemberLvUpOrderVO> listMemberLvUpOrder(OrderQuery query);

    /**
     * @Description 根据订单号查询订单
     * @Author  Kaven
     * @Date   2019/12/9 16:55
     * @Param  orderNo
     * @Return OrderEntity
    */
    OrderEntity queryByOrderNo(String orderNo);

    /**
     * 更新订单状态
     * @param orderNo
     * @param orderStatus
     * @param updateUser
     * @param updateTime
     */
    void updateOrderStatus(@Param("orderNo") String orderNo, @Param("orderStatus") Integer orderStatus,
                           @Param("updateUser") String updateUser, @Param("updateTime") Date updateTime);

    /**
     * 更新订单状态
     * @param orderNo
     * @param channelPushState
     */
    void updateOrderChannelPushState(@Param("orderNo") String orderNo, @Param("channelPushState") Integer channelPushState);

    /**
     * 批量更新订单推送状态
     *
     *
     */
    void batchUpdateOrderChannelPushState();

    /**
     * 批量更新订单推送状态
     *
     *
     */
    void batchUpdateOrderChannelPushStateByOrderNo(List<OpenOrderVO> listToBePush,Integer channelPushState);


    Map<String, Integer> sumInvOrder(OrderQuery query);

    /**
     * 发货订单导出
     * @param query
     * @return
     */
    List<InvOrderBatchShipmentsVO> listInvBatchStock(OrderQuery query);

    void updateBatchOrderStatus(@Param("orders") List<String> orders, @Param("orderStatus") Integer orderStatus,
                                @Param("updateUser") String updateUser, @Param("updateTime") Date updateTime);
    /**
     * 查询会员是否已经升级
     * @param memberId 会员id
     * @param productType 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-税务顾问 10-城市服务商
     * @param orderType 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商开户 6-开票 7-用户升级 8-工商注销
     * @param userType 用户类型 1->会员 2->城市合伙人 3->合伙人 4->平台 5->管理员 6->其他
     * @return
     */
    List<OrderEntity> queryMemberLvUpOrder(@Param("memberId") Long memberId, @Param("productType") Integer productType,
                                           @Param("orderType") Integer orderType, @Param("userType") Integer userType,
                                           @Param("auditStatus") Integer auditStatus, @Param("orderStatus") Integer orderStatus);

    /**
     * 查询消费钱包账单明细列表
     * @param memberId
     * @param oemCode
     * @param walletType
     * @param type
     * @param month
     * @return
     */
    List<BillDetailVO> listConsumerBillDetail(@Param("memberId") Long memberId, @Param("oemCode") String oemCode,
                                              @Param("type") Integer type, @Param("month") String month,
                                              @Param("walletType") Integer walletType, @Param("status") Integer status);

    /**
     * 查询佣金钱包账单明细列表
     * @param memberId
     * @param oemCode
     * @param walletType
     * @param type
     * @param month
     * @return
     */
    List<BillDetailVO> listCommissionBillDetail(@Param("memberId") Long memberId, @Param("oemCode") String oemCode,
                                                @Param("type") Integer type, @Param("month") String month,
                                                @Param("walletType") Integer walletType, @Param("status") Integer status);

    /**
     * 查询账单退款金额
     * @param memberId
     * @param oemCode
     * @param walletType
     * @param type
     * @return
     */
    Long billRefundAmount(@Param("memberId") Long memberId, @Param("oemCode") String oemCode,
                          @Param("type") Integer type, @Param("month") String month,
                          @Param("walletType") Integer walletType);

    /**
     * 查询24小时未支付的开票订单
     * @return
     */
    List<OrderEntity> invOrderListByType();

    /**
     * 查询昨日对公申请未支付的开票订单
     * @return
     */
    List<OrderEntity> invOrderListByOrderType();

    /**
     * 查询企业注销订单
     * @param query
     * @return
     */
    List<CompanyCancelOrderVO> listCancelOrder(OrderQuery query);

    /**
     * @Description 查询指定用户和公司ID下所有不是已取消状态的企业注销订单
     * @Author  Kaven
     * @Date   2020/2/19 11:39
     * @Param  userId oemCode companyId
     * @Return list
     * @Exception
    */
    List<OrderEntity> queryComCancelOrder(@Param("userId") Long userId, @Param("oemCode") String oemCode
            , @Param("companyId") Long companyId, @Param("orderStatus") Integer orderStatus);

    /**
     * @Description 会员升级订单查询-拓展宝
     * @Author  Kaven
     * @Date   2020/3/25 16:57
     * @Param   TZBOrderQuery
     * @Return  List<MemberUpgradeOrderVO>
     * @Exception
    */
    List<MemberUpgradeOrderVO> queryMemberLvUpOrderList(TZBOrderQuery query);

    /**
     * 根据会员查询订单
     * @param memberId
     * @param oemCode
     * @param orderType
     * @param orderStatus
     * @return
     */
    OrderEntity queryByMemberId(@Param("memberId") Long memberId, @Param("oemCode") String oemCode, @Param("orderType") Integer orderType, @Param("orderStatus") Integer orderStatus);

    /**
     *
     * @param query
     * @return
     */
    List<BatchInvOrderExportVO> invBatchExportOrderList(OrderQuery query);
    /**
     *批量下载办理信息
     * @param query
     * @return
     */
    List<OpenOrderExportVO> getopenOrderExportList(OrderQuery query);

    /**
     * @Description 查询会员升级订单列表（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 2:01 下午
     * @Param   MemberExtendQuery
     * @Return  List<ExtendMemberVO>
     * @Exception
    */
    List<ExtendMemberVO> queryMemberUpgradeOrderList(MemberExtendQuery query);
    /**
     * @Description 查询会员升级订单列表（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 2:01 下午
     * @Param   MemberExtendQuery
     * @Return  List<ExtendMemberVO>
     * @Exception
     */
    List<ExtendMemberVO> queryMemberUpgradeOrderListByChannelServiceId(MemberExtendQuery query);

    /**
     * @Description 查询会员升级订单列表（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 2:01 下午
     * @Param   MemberExtendQuery
     * @Return  List<ExtendMemberVO>
     * @Exception
     */
    List<ExtendMemberVO> queryContComRegExtendDetailByChannelServiceId(MemberExtendQuery query);
    /**
     * @Description 查询总会费（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 2:01 下午
     * @Param   MemberExtendQuery
     * @Return  Long
     * @Exception
    */
    Long queryTotalMemberFee(MemberExtendQuery query);

    /**
     * @Description 查询总会费（推广明细）
     * @Param   MemberExtendQuery
     * @Return  Long
     * @Exception
     */
    Long queryTotalMemberFeeByChannelServiceId(MemberExtendQuery query);
    /**
     * @Description 查询托管费续费总会费（推广明细）
     * @Param   MemberExtendQuery
     * @Return  Long
     * @Exception
     */
    Long queryTotalContRegFeeByChannelServiceId(MemberExtendQuery query);

    /**
     * 根据集团代开订单编号修改状态
     * @param groupOrderNo
     * @param oemCode
     * @param orderStatus
     * @param updateUser
     * @param updateTime
     * @return
     */
    void updateStatusByGroupOrderNo(@Param("groupOrderNo")String groupOrderNo, @Param("oemCode")String oemCode, @Param("orderStatus")Integer orderStatus,
                                    @Param("updateUser")String updateUser, @Param("updateTime")Date updateTime);

    /**
     * @Description 更新订单状态和外部订单号
     * @Author Kaven
     * @Date 2020/9/9 11:46
     * @Param externalOrderNo orderStatus orderNo
     * @Return
     * @Exception
     */
    void updateOrderStatusAndExternalOrderNo(@Param("externalOrderNo") String externalOrderNo, @Param("orderStatus") Integer orderStatus,
                                             @Param("orderNo") String orderNo, @Param("updateTime") Date updateTime);

    /**
     * 查询会员是否有支付的会员升级订单
     *
     * @param memberId
     * @param oemCode
     * @return
     */
    OrderEntity queryUpgradeOrder(@Param("memberId") Long memberId, @Param("oemCode") String oemCode);

    /**
     * @Description 查询指定时间前的待提交对公户提现订单列表
     * @Author Kaven
     * @Date 2020/10/16 14:26
     * @Param minutes
     * @Return List<OrderEntity>
     * @Exception
     */
    List<OrderEntity> selectToSubmitWithdrawOrder(@Param("minutes") Integer minutes);

    /**
     * 查询钱包账单明细列表API
     *
     * @param memberId
     * @param oemCode
     * @param walletType
     * @param type
     * @param month
     * @param status
     * @return
     */
    List<BillDetailVO> listBillDetailApi(@Param("memberId") Long memberId, @Param("oemCode") String oemCode,
                                         @Param("type") Integer type, @Param("month") String month,
                                         @Param("walletType") Integer walletType, @Param("status") Integer status);

    /**
     * 查询订单
     * @param productId
     * @param oemCode
     * @param orderType
     * @param orderStatus
     * @return
     */
    List<OrderEntity> queryByProductId(@Param("productId")Long productId, @Param("oemCode")String oemCode, @Param("orderType")Integer orderType, @Param("orderStatus")Integer orderStatus);

    /**
     * 统计对公户提现重复金额订单
     * @param orderType
     * @param exceptOrderStatus
     * @param payAmount
     * @param repeatTime
     */
    Integer corporateAmoutRepeatCheck(@Param("orderType")Integer orderType, @Param("exceptOrderStatus")Integer exceptOrderStatus, @Param("payAmount")Long payAmount, @Param("repeatTime")String repeatTime,@Param("corporateAccountId")Long corporateAccountId);

    /**
     * 获取作废/红冲订单信息
     * @param orderNo
     * @return
     */
    OrderVoidInfo getToVoidInfo(@Param("orderNo") String orderNo);

    /**
     * 根据企业id查询在税单待处理、待付款的注销订单的订单号
     * @param companyId
     * @return
     */
    List<String> getOderNoByCompanyAndOrderStatus(@Param("companyId") Long companyId);

    /**
     * 根据企业id查询非取消状态注销订单
     * @param companyId
     * @return
     */
    List<String> getOderNoByCompany(@Param("companyId") Long companyId);

    /**
     * 用户月累计消费钱包提现金额
     * @param memberId
     * @return
     */
    Long monthlyWithdrawalAmount(@Param("memberId") Long memberId, @Param("year") int year, @Param("month") int month);

    /**
     * 查询未达充值天数的充值金额
     * @param userId
     * @param rechargeDays
     * @return
     */
    Long queryRechargeAmountOfDays(@Param("userId") Long userId, @Param("rechargeDays") Integer rechargeDays);
}

