package com.yuqian.itax.order.dao;

import com.yuqian.itax.common.base.dao.BaseMapper;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.query.ParkRewardQuery;
import com.yuqian.itax.order.entity.query.TZBOrderQuery;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.user.entity.query.ExtendUserQuery;
import com.yuqian.itax.user.entity.query.MemberExtendQuery;
import com.yuqian.itax.user.entity.vo.ComInvExtendDetailVO;
import com.yuqian.itax.user.entity.vo.ExtendMemberVO;
import com.yuqian.itax.user.entity.vo.ExtendUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 开票订单dao
 * 
 * @Date: 2019年12月07日 20:05:12 
 * @author yejian
 */
@Mapper
public interface InvoiceOrderMapper extends BaseMapper<InvoiceOrderEntity> {

    /**
     * @Description 查询待通知的开票订单列表
     * @Author  Kaven
     * @Date   2019/12/9 15:57
     * @Param  userId
     * @Return List
    */
    List<OrderVO> queryNoticeInvOrderList(Long userId);

    /**
     * @Description 根据订单号查询订单
     * @param  orderNo
     * @Return InvoiceOrderEntity
     */
    InvoiceOrderEntity queryByOrderNo(String orderNo);

    /**
     * 根据订单号查询订单快递信息
     * @param orderNo
     * @return
     */
    InvCourierVo queryCourierByOrderNo(String orderNo);

    /**
     * 查询开票订单列表
     * @param memberId
     * @param oemCode
     * @param companyId
     * @param month
     * @param type
     * @param pointTime V4.0历史订单划定时间点
     * @return
     */
    List<InvoiceOrderVO> listInvoiceOrder(@Param("memberId")Long memberId, @Param("oemCode")String oemCode,
                                          @Param("companyId")Long companyId, @Param("month")String month, @Param("type")Long type,
                                          @Param("pointTime") String pointTime);

    /**
     * 查询本年开票订单列表
     * @param memberId
     * @param oemCode
     * @param parkId
     * @param companyId
     * @param year
     * @return
     */
    List<InvoiceOrderEntity> InvOrderListOfYear(@Param("memberId")Long memberId, @Param("oemCode")String oemCode,
                                                @Param("parkId")Long parkId, @Param("companyId")Long companyId, @Param("year")String year);

    /**
     * 查询本月或本季开票订单列表
     * @param memberId
     * @param oemCode
     * @param parkId
     * @param companyId
     * @param month
     * @param startDate
     * @param endDate
     * @param orderNo
     * @return
     */
    List<InvoiceOrderEntity> InvOrderListOfDate(@Param("memberId")Long memberId, @Param("oemCode")String oemCode,
                                                @Param("parkId")Long parkId, @Param("companyId")Long companyId,
                                                @Param("month")String month, @Param("startDate")String startDate,
                                                @Param("endDate")String endDate, @Param("orderNo")String orderNo);


    /**
     * 查询历史存量开票订单额度
     * @param memberId
     * @param oemCode
     * @param parkId
     * @param companyId
     * @param month
     * @param startDate
     * @param endDate
     * @param invoiceType
     * @param orderNo
     * @return
     */
    int cycInvoiceOrderAmountByDate(@Param("memberId")Long memberId, @Param("oemCode")String oemCode,
                                    @Param("parkId")Long parkId, @Param("companyId")Long companyId,
                                    @Param("month")String month, @Param("startDate")String startDate,
                                    @Param("endDate")String endDate, @Param("invoiceType")Integer invoiceType,
                                    @Param("orderNo")String orderNo);

    /**
     * 查询历史存量开票订单列表
     * @param memberId
     * @param oemCode
     * @param parkId
     * @param companyId
     * @param month
     * @param startDate
     * @param endDate
     * @param orderNo
     * @return
     */
    List<InvoiceOrderEntity> cycInvoiceOrderListByDate(@Param("memberId")Long memberId, @Param("oemCode")String oemCode,
                                    @Param("parkId")Long parkId, @Param("companyId")Long companyId,
                                    @Param("month")String month, @Param("startDate")String startDate,
                                    @Param("endDate")String endDate, @Param("orderNo")String orderNo);

    /**
     * 根据订单号查询订单详情
     * @param memberId
     * @param orderNo
     * @return
     */
    InvoiceOrderDetailVO queryDetailByOrderNo(@Param("memberId")Long memberId, @Param("orderNo")String orderNo);

    /**
     * 根据订单状态查询开票订单列表
     * @return
     */
    List<InvoiceOrderDetailVO> listInvoiceOrderDetailByStatus();

    /**
     * 根据企业id查询未完成订单数量
     * @param companyId
     * @param oemCode
     * @return
     */
    int queryNotFinishOrderByCompanyId(@Param("companyId")Long companyId, @Param("oemCode")String oemCode);


    /**
     * 根据企业id查询带创建，待付款，待审核，出票中的订单数量
     * @param companyId
     * @param oemCode
     * @return
     */
    int querySomeStatusOrderByCompanyId(@Param("companyId")Long companyId, @Param("oemCode")String oemCode);

    /**
     * 根据月份和企业id查询企业开票额度
     * @param memberId
     * @param oemCode
     * @param companyId
     * @param month
     * @return
     */
    int invoiceAmountByDate(@Param("memberId")Long memberId, @Param("oemCode")String oemCode, @Param("companyId")Long companyId, @Param("month")String month);

    /**
     * 查询开票订单数量
     * @param memberId
     * @param oemCode
     * @param companyId
     * @param month
     * @return
     */
    InvoiceOrderCountVO getInvOrderCount(@Param("memberId")Long memberId, @Param("oemCode")String oemCode, @Param("companyId")Long companyId, @Param("month")String month);

    /**
     * 根据集团开票订单号查询所有开票信息
     * @param groupOrderNo
     * @param oemCode
     * @return
     */
    List<InvoiceOrderByGroupOrderNoVO> listByGroupOrderNo(@Param("groupOrderNo")String groupOrderNo, @Param("oemCode")String oemCode);

    /**
     * 根据集团开票订单号查询所有开票信息
     * @param groupOrderNo
     * @return
     */
    List<InvoiceOrderByGroupOrderNoVO> listByGroupOrderNoInfo(@Param("groupOrderNo")String groupOrderNo);

    /**
     * 根据集团开票订单查询开票子订单
     * @param groupOrderNo
     * @param oemCode
     * @param notOrderStatus
     */
    List<InvoiceOrderEntity> queryByGroupOrderNo(@Param("groupOrderNo")String groupOrderNo, @Param("oemCode")String oemCode,
                                                 @Param("notOrderStatus")String notOrderStatus);

    /**
     * 根据集团订单号统计开票信息
     * @param groupOrderNo
     * @param oemCode
     * @param orderStatus
     * @return
     */
    InvoiceSumInfoVO sumInvoiceInfo(@Param("groupOrderNo")String groupOrderNo, @Param("oemCode")String oemCode,
                                    @Param("orderStatus")Integer orderStatus);

    /**
     * 更新集团订单编号
     * @param groupOrderNo
     * @param orderNo
     * @param oemCode
     */
    void updateGroupOrderNo(@Param("groupOrderNo")String groupOrderNo, @Param("orderNo")String orderNo, @Param("oemCode")String oemCode);

    /**
     * @Description 开票订单查询-拓展宝
     * @Author  Kaven
     * @Date   2020/3/25 16:09
     * @Param   TZBOrderQuery
     * @Return  List<InvoiceOrdVO>
     * @Exception
    */
    List<InvoiceOrdVO> queryInvoiceOrder(TZBOrderQuery query);

    /**
     * 查询开票订单未付款列表
     * @param memberId
     * @param oemCode
     * @param companyId
     * @return
     */
    List<OrderNoVO> getUnpaidList(@Param("memberId")Long memberId, @Param("oemCode")String oemCode, @Param("companyId")Long companyId);

    /**
     * 查询开票订单补传流水列表
     * @param memberId
     * @param oemCode
     * @param type
     * @return
     */
    List<InvoiceWaterOrderVO> listInvoiceWaterOrder(@Param("memberId")Long memberId, @Param("oemCode")String oemCode, @Param("type")Long type);


    /**
     * 查询开票订单补传流水列表
     * @param memberId
     * @param oemCode
     * @param type
     * @return
     */
    List<InvoiceAchievementOrderVO> listInvoiceAchievementOrder(@Param("memberId")Long memberId, @Param("oemCode")String oemCode, @Param("type")Long type);

    /**
     * 查询开票补传流水订单详情
     * @param memberId
     * @param oemCode
     * @param orderNo
     * @return
     */
    InvoiceWaterOrderVO getInvWaterDetail(@Param("memberId")Long memberId, @Param("oemCode")String oemCode, @Param("orderNo")String orderNo);
    /**
     * 查询开票补传流水订单详情
     * @param memberId
     * @param oemCode
     * @param orderNo
     * @return
     */
    InvoiceAchievementOrderVO getInvAchievementDetail(@Param("memberId")Long memberId, @Param("oemCode")String oemCode, @Param("orderNo")String orderNo);

    /**
     * 获取待补传流水集合
     * @param map
     * @return
     */
    List<InvoiceWaterOrderVO> getWaterOrder(Map<String, Object> map);

    /**
     * 获取待补传成果集合
     * @param map
     * @return
     */
    List<InvoiceAchievementOrderInfoVO> getAchievementOrder(Map<String, Object> map);

    /**
     * @Description 查询订单状态已签收超过overDays个自然日，但流水/成果状态还是待补传或者审核未通过的开票订单
     * @Author  Kaven
     * @Date   2020/5/20 15:16
     * @Param   overDays
     * @Return  List<InvoiceOrderEntity>
     * @Exception
    */
    List<InvoiceNoticeVO> selectUploadFlowTimeoutOrder(@Param("overDays")Integer overDays);

    /**
     * @Description 更新开票订单通知状态
     * @Author  Kaven
     * @Date   2020/5/21 10:23
     * @Param   userId oemCode overDays
     * @Return
     * @Exception
    */
    void updateAlertNumber(@Param("userId")Long userId, @Param("oemCode")String oemCode, @Param("overDays")Integer overDays);
    /**
     * 根据企业Id统计历史累计开票金额
     * @param companyId
     * @return
     */
    Long sumOrderAmountByCompanyId(@Param("companyId")Long companyId);

    List<Long> queryInvoiceReach(@Param("userId") Long userId,@Param("oemCode")String oemCode,@Param("minInvoiceAmount")Long minInvoiceAmount);

    List<Long> queryCompanyRegistReach(@Param("userId") Long userId,@Param("oemCode")String oemCode);


    /**
     * @Description 推广中心-业绩总览-直推用户列表查询
     * @Author  Kaven
     * @Date   2020/6/5 16:08
     * @Param ExtendUserQuery
     * @Return
     * @Exception
    */
    List<ExtendUserVO> listDirectUsers(ExtendUserQuery query);

    /**
     * @Description 根据订单状态查询企业开票订单列表（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 2:30 下午
     * @Param   MemberExtendQuery
     * @Return List<ExtendMemberVO>
     * @Exception
    */
    List<ExtendMemberVO> queryInvOrderListByStatus(MemberExtendQuery query);

    /**
     * @Description 根据订单状态查询企业开票订单列表（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 2:30 下午
     * @Param   MemberExtendQuery
     * @Return List<ExtendMemberVO>
     * @Exception
     */
    List<ExtendMemberVO> queryInvOrderListByStatusByChannelServiceId(MemberExtendQuery query);

    /**
     * @Description 查询总开票金额和总服务费（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 2:31 下午
     * @Param   MemberExtendQuery
     * @Return  ComInvExtendDetailVO
     * @Exception
    */
    ComInvExtendDetailVO queryTotalInvServiceFee(MemberExtendQuery query);

    /**
     * @Description 查询总开票金额和总服务费（推广明细）
     * @Author  Kaven
     * @Date   2020/6/8 2:31 下午
     * @Param   MemberExtendQuery
     * @Return  ComInvExtendDetailVO
     * @Exception
     */
    ComInvExtendDetailVO queryTotalInvServiceFeeByChannelServiceId(MemberExtendQuery query);

    /**
     * 根据集团代开订单编号修改开票信息
     * @param entity
     */
    void updateByGroupOrderNo(InvoiceOrderEntity entity);

    /**
     * 查询开票订单补传流水列表
     * @param oemCode
     * @param orderNo
     * @param companyName
     * @param bankWaterStatus
     * @param regPhone
     * @return
     */
    List<InvoiceWaterOrderApiVO> getInvWaterOrderListByQuery(@Param("oemCode")String oemCode, @Param("orderNo")String orderNo,
                                                       @Param("companyName")String companyName, @Param("bankWaterStatus")Integer bankWaterStatus,
                                                             @Param("regPhone")String regPhone);

    /**
     * @Description 更新开票订单剩余可提现额度
     * @Author  Kaven
     * @Date   2020/9/9 14:52
     * @Param   invoiceOrderNo withdrawalAmount flag
     * @Return
     * @Exception
    */
    void updateRemainWithdrawAmount(@Param("invoiceOrderNo") String invoiceOrderNo,@Param("withdrawalAmount") Long withdrawalAmount, @Param("flag")Integer flag);

    /**
     * 查询开票统计视图
     * @param userId
     * @param companyId
     */
    InvoiceStatisticsViewVO queryCompanyInvoiceRecordStatisticsView(@Param("userId")Long userId, @Param("companyId")Long companyId);
    /**
     *
     */
    List<Map<String,Object>> sumByGroupOrderNo(@Param("groupOrderNo") String groupOrderNo);
    /**
     * 查询时间范围内累计开票金额（已完成）根据增值税分组
     */
    List<Map<String,Object>> queryTotaLInvoiceAmountByVat(@Param("start") String start,@Param("end") String end,@Param("parkId")Long parkId,@Param("invoiceType") Integer invoiceType,@Param("companyId")Long companyId);
    /**
     * 查询税期范围内累计开票金额（已完成）根据增值税分组
     */
    List<Map<String,Object>> queryTotaLInvoiceAmountByVatByTaxYear(@Param("year") Integer year,@Param("seasonal") Integer seasonal,@Param("parkId")Long parkId,@Param("invoiceType") Integer invoiceType,@Param("companyId")Long companyId);

    /**
     * 查询税期范围内累计开票金额（已完成）所得税
     */
    Long queryTotaLInvoiceAmountByIit(@Param("year") Integer year,@Param("seasonal") Integer seasonal,@Param("parkId")Long parkId,@Param("invoiceType") Integer invoiceType,@Param("companyId")Long companyId);
    /**
     * 查询时间范围内累计开票金额（已完成）附加税
     */
    Long queryTotaLInvoiceAmountByFj(@Param("start") String start,@Param("end") String end,@Param("parkId")Long parkId,@Param("invoiceType") Integer invoiceType,@Param("companyId")Long companyId);

    /**
     * 周期内已开票金额
     * @param companyId
     * @param oemCode
     * @return
     */
    List<CountPeriodInvoiceAmountVO> countPeriodInvoiceAmount(@Param("start") Date start, @Param("end") Date end, @Param("companyId") Long companyId, @Param("oemCode") String oemCode, @Param("invoiceType")Integer invoiceType);

    /**
     * 周期内已缴税费
     * @param start
     * @param end
     * @param companyId
     * @param oemCode
     * @return
     */
    Long periodPaidTax(@Param("start") Date start, @Param("end") Date end, @Param("companyId") Long companyId, @Param("oemCode") String oemCode, @Param("type") Integer type);

    /**
     * 查询订单详情并包装数据
     * @param memberId
     * @param orderNo
     * @return
     */
    InvoiceOrderSubpackageVO querySubpackageByOrderNo(long memberId, String orderNo);

    /**
     * 根据企业税号查询企业累计开票金额（按增值税率分组）
     * @param companyId 企业id
     * @param type 适用类型 1-企业开票 2-企业税单 3-企业预税单
     * @param start 开始时间
     * @param end 结束时间
     * @param invoiceType 发票类型 1-增值税普通发票 2-增值税专用发票
     * @param isCurrentCycle 是否当前周期 0-否 1-是
     * @param isAcrossQuarter 是否跨季
     * @return
     */
    List<CountPeriodInvoiceAmountVO> queryCompanyInvoiceAmountByEin(@Param("companyId") Long companyId, @Param("type") int type, @Param("start") Date start,
                                                                    @Param("end") Date end, @Param("invoiceType") Integer invoiceType,
                                                                    @Param("isCurrentCycle") Integer isCurrentCycle, @Param("isAcrossQuarter") Integer isAcrossQuarter);

    /**
     * 根据企业税号查询企业累计已缴税费
     * @param companyId
     * @param type
     * @param start
     * @param end
     * @return
     */
    PeriodPaidTaxVo paidTax(@Param("companyId") Long companyId, @Param("type") int type, @Param("start") Date start, @Param("end") Date end);

    /**
     * 获取开票支付信息
     * @param orderNo
     * @return
     */
    InvoicePayInfo queryInvoicePayInfo(String orderNo);

    /**
     * 第三方查询开票订单
     * @param accessPartyCode
     * @param orderNo
     * @return
     */
    List<ThirdPartyQueryInoiveInfoVO> thirdPartyQueryInvoiceList(@Param("orderNo") String orderNo, @Param("externalOrderNo") String externalOrderNo, @Param("accessPartyCode") String accessPartyCode);

    /**
     * 查询
     * @param orderNo
     * @param oemCode
     * @return
     */
    String queryCancellationVoucher(@Param("orderNo") String orderNo, @Param("oemCode") String oemCode);

    /**
     * 查询企业作废/红冲金额
     * @param companyId
     * @param start
     * @param end
     * @return
     */
    Long queryCancellationAmount(@Param("companyId") Long companyId, @Param("start") Date start, @Param("end") Date end);

    /**
     * 基于当前订单查询其他开票订单
     * @param companyId
     * @param afterTheTime 晚于当前时间
     * @param beforeTheTime 早于当前时间
     * @param serviceFeeRate 服务费费率
     * @return
     */
    List<String> queryExistPendingInvOrder(@Param("companyId") Long companyId, @Param("afterTheTime") Date afterTheTime, @Param("beforeTheTime") Date beforeTheTime,
                                           @Param("serviceFeeRate") BigDecimal serviceFeeRate, @Param("orderNo") String orderNo);

    /**
     * 查询企业在当前时间之后创建的订单
     * @param companyIdList
     * @param addTime
     * @return
     */
    List<InvoiceOrderEntity> findExistPendingOrder(@Param("companyIdList") List<Long> companyIdList,@Param("addTime") Date addTime,@Param("groupOrderNo") String groupOrderNo);

    /**
     * 查询个体户指定状态订单
     * @param companyId
     * @param orderStatus
     * @return
     */
    List<String> queryByOrderStatus(@Param("companyId") Long companyId, @Param("orderStatus") Integer orderStatus);

    /**
     * 根据企业id获取佣金开票状态为待出款的订单
     * @param company
     * @return
     */
    List<String> getOrderNoByCreateWayAndCompanyId(@Param("companyId") Long company);

    /**
     * 根据企业类型园区id查询当季开票金额
     * @param companyId
     * @param parkId
     * @return
     */
    Long getquarterAmountByCompanyTypeAndParkId(@Param("companyId") Long companyId,@Param("parkId") Long parkId);

    /**
     * 校验待财务审核开票订单数据
     * @param companyId 企业id
     * @param updateTime 更新时间
     * @param isBefore 是否当前待财务审核之前的数据  1-当前订单之前 2-当前订单之后
     * @param orderNo 订单号
     * @return
     */
    Integer checkInvoicePaymentReview(@Param("companyId") Long companyId,@Param("updateTime") Date updateTime,@Param("isBefore") Integer isBefore,@Param("orderNo") String orderNo);

    /**
     * 查询园区奖励
     * @param query
     * @return
     */
    List<ParkRewardVO> queryParkReward(ParkRewardQuery query);
}