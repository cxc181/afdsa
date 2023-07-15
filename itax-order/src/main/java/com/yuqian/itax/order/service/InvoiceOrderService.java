package com.yuqian.itax.order.service;

import com.github.pagehelper.PageInfo;
import com.yuqian.itax.capital.entity.dto.UserWithdrawDTO;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.group.entity.GroupPaymentAnalysisRecordEntity;
import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
import com.yuqian.itax.order.dao.InvoiceOrderMapper;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.MemberOrderRelaEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.dto.*;
import com.yuqian.itax.order.entity.query.InvOrderQuery;
import com.yuqian.itax.order.entity.query.InvWaterOrderQuery;
import com.yuqian.itax.order.entity.query.InvoiceWaterOrderApiQuery;
import com.yuqian.itax.order.entity.query.TZBOrderQuery;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.park.entity.query.TaxRulesVatRateQuery;
import com.yuqian.itax.park.entity.vo.TaxRulesVatRateVO;
import com.yuqian.itax.tax.entity.vo.TaxCalculationVO;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.query.CompanyListApiQuery;
import com.yuqian.itax.user.entity.query.CompanyListQuery;
import com.yuqian.itax.user.entity.query.ExtendUserQuery;
import com.yuqian.itax.user.entity.vo.CompanyListApitVO;
import com.yuqian.itax.user.entity.vo.ExtendUserVO;
import com.yuqian.itax.user.entity.vo.MemberCompanyVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开票订单service
 * 
 * @Date: 2019年12月07日 20:05:12 
 * @author yejian
 */
public interface InvoiceOrderService extends IBaseService<InvoiceOrderEntity,InvoiceOrderMapper> {


    /**
     * @Description 根据订单号查询订单
     * @param orderNo
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
     * @param query
     * @return List
     */
    List<InvoiceOrderVO> listInvoiceOrder(Long memberId, String oemCode, InvOrderQuery query);

    /**
     * 出票
     * @param invEntity 出票实体
     * @param userAccount 操作人
     * @param memberId 出票所属用户
     */
    void ticket(InvoiceOrderEntity invEntity, String userAccount, Long memberId);

    /**
     * 取消开票订单
     * @param memberId
     * @param oemCode
     * @param orderNo
     * @return
     */
    void cancelInvOrder(Long memberId, String oemCode, String orderNo) throws BusinessException;

    /**
     * 根据订单号查询订单详情
     * @param memberId
     * @param orderNo
     * @Return InvoiceOrderDetailVO
     */
    InvoiceOrderSubpackageVO queryDetailByOrderNo(long memberId, String orderNo) throws BusinessException;

    /**
     * @param memberId
     * @param oemCode
     * @param entity
     * @param sourceType 操作小程序来源 1-微信小程序 2-支付宝小程序
     * @param isResubmit 是否是重新提交开票订单
     * @Description 创建开票订单
     * @Return String
     */
    String createInvoiceOrder(Long memberId, String oemCode, CreateInvoiceOrderDTO entity, String sourceType, boolean isResubmit) throws BusinessException;

    /**
     * @Description 确认开票订单
     * @param memberId
     * @param oemCode
     * @param entity
     * @Return Map
     */
    Map<String,Object> confirmInvoiceOrder(Long memberId, String oemCode, ConfirmInvoiceOrderDTO entity) throws BusinessException;

//    /**
//     * @Description 计算支付金额
//     * @param memberId
//     * @param oemCode
//     * @param orderNo
//     * @param vatRate 增值税税率
//     * @Return InvoiceOrderPayAmountVO
//     */
//    InvoiceOrderPayAmountVO countPayAmount(Long memberId, String oemCode, String orderNo, BigDecimal vatRate) throws BusinessException;

    /**
     * @Description 新余额支付
     * @param memberId
     * @param oemCode
     * @param entity
     * @Return String
     * @Date 2021/03/03
     */
    Map<String,String> newBalancePayOrder(Long memberId, String oemCode, InvOrderPayDTO entity) throws BusinessException;

    /**
     * @Description 查询用户关系
     * @param memberId
     * @param oemCode
     * @param type 1-开户 2-开票 3-会员升级 4-企业注销 5-证件领用
     * @Return MemberOrderRelaEntity
     */
    MemberOrderRelaEntity getUserTree(Long memberId, String oemCode, Integer type) throws BusinessException;

    /**
     * @Description 开票订单确认收货
     * @param memberId
     * @param oemCode
     * @param orderNo
     * @Return
     */
    void confirmReceipt(Long memberId, String oemCode, String orderNo,String updateUser) throws BusinessException;

    /**
     * 24小时未支付自动取消开票订单
     * @param orderNo
     * @Return
     */
    void xxljobCancelInvOrder(String orderNo);

    /**
     * 根据订单状态查询开票订单列表
     * @Return List
     */
    List<InvoiceOrderDetailVO> listInvoiceOrderDetailByStatus();

    /**
     * 根据订单号查询开票订单进度
     * @param memberId
     * @param orderNo
     * @Return Map
     */
    Map<String,Object> getOrderProgress(long memberId, String orderNo) throws BusinessException;

    /**
     * 根据企业id查询未完成订单数量
     * @param companyId
     * @param oemCode
     * @Return int
     */
    int queryNotFinishOrderByCompanyId(Long companyId, String oemCode);

    /**
     * 根据企业id查询带创建，待付款，待审核，出票中的订单数量
     * @param companyId
     * @param oemCode
     * @Return int
     */
    int querySomeStatusOrderByCompanyId(Long companyId, String oemCode);

    /**
     * 根据月份和企业id查询企业开票额度
     * @param memberId
     * @param oemCode
     * @param companyId
     * @param month
     * @Return int
     */
    int invoiceAmountByDate(Long memberId, String oemCode, Long companyId, String month);

    /**
     * 查询开票订单数量
     * @param memberId
     * @param oemCode
     * @param companyId
     * @param month
     * @Return InvoiceOrderCountVO
     */
    InvoiceOrderCountVO getInvOrderCount(Long memberId, String oemCode, Long companyId, String month);

    /**
     * 查询我的企业列表
     * @return
     */
    List<MemberCompanyVo> listMemberCompany(Long memberId, String oemCode, CompanyListQuery query);

    /**
     * 根据集团开票订单号分页查询开票信息
     * @param groupOrderNo
     * @param oemCode
     * @param pageNumber
     * @param pageSize
     * @return
     */
    PageInfo<InvoiceOrderByGroupOrderNoVO> pageListByGroupOrderNo(String groupOrderNo, String oemCode, Integer pageNumber, Integer pageSize);

    /**
     * 根据集团开票订单号查询所有开票信息
     * @param groupOrderNo
     * @param oemCode
     * @return
     */
    List<InvoiceOrderByGroupOrderNoVO> listByGroupOrderNo(String groupOrderNo, String oemCode);

    /**
     * 创建企业开票子订单
     * @param company
     * @param analysisEntity
     * @param entity
     */
    InvoiceOrderEntity createInvoiceOrderByGroup(MemberCompanyVo company, GroupPaymentAnalysisRecordEntity analysisEntity, InvoiceOrderGroupEntity entity);

    /**
     * @Description 查询开票订单-拓展宝
     * @Author  Kaven
     * @Date   2020/3/25 15:51
     * @Param   query
     * @Return  PageResultVo<InvoiceOrdVO>
     * @Exception
    */
    PageResultVo<InvoiceOrdVO> queryInvoiceOrder(TZBOrderQuery query);

    /**
     * 查询开票订单未付款列表
     * @param memberId
     * @param oemCode
     * @param companyId
     * @return List
     */
    List<OrderNoVO> getUnpaidList(Long memberId, String oemCode, Long companyId);

    /**
     * 查询个体户指定状态开票订单
     * @param companyId
     * @param orderStatus
     * @return
     */
    List<String> findByOrderStatus(Long companyId, Integer orderStatus);

    /**
     * 校验订单是否跨月/季处理
     * @param memberId
     * @param oemCode
     * @param orderNo
     * @Return Map
     */
    Map<String,Object> checkCrossTime(long memberId, String oemCode, String orderNo) throws BusinessException;

    /**
     * 查询开票补传流水订单数量
     * @param memberId
     * @param oemCode
     * @return int
     */
    InvoiceWaterCountVO countInvoiceWaterOrder(Long memberId, String oemCode);

    /**
     * 查询开票订单补传流水列表
     * @param memberId
     * @param oemCode
     * @param query
     * @return List
     */
    List<InvoiceWaterOrderVO> listInvoiceWaterOrder(Long memberId, String oemCode, InvWaterOrderQuery query);

    /**
     * 查询开票订单补传流水列表
     * @param memberId
     * @param oemCode
     * @param query
     * @return List
     */
    List<InvoiceAchievementOrderVO> listInvoiceAchievementOrder(Long memberId, String oemCode, InvWaterOrderQuery query);

    /**
     * 查询开票补传流水订单详情
     * @param memberId
     * @param oemCode
     * @param orderNo
     * @return InvoiceWaterOrderVO
     */
    InvoiceWaterOrderVO getInvWaterDetail(Long memberId, String oemCode, String orderNo) throws BusinessException;

    /**
     * 查询开票补传流水订单详情
     * @param memberId
     * @param oemCode
     * @param orderNo
     * @return InvoiceWaterOrderVO
     */
    InvoiceAchievementOrderVO getInvAchievementDetail(Long memberId, String oemCode, String orderNo) throws BusinessException;

    /**
     * 开票订单补传流水
     * @param memberId
     * @param oemCode
     * @param entity
     */
    void patchBankWater(Long memberId, String oemCode, InvOrderBankWaterDTO entity);

    /**
     * 开票订单补传成果
     * @param memberId
     * @param oemCode
     * @param entity
     */
    void patchBankAchievement(Long memberId, String oemCode, InvOrderBankAchievementDTO entity);
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
     * @Date   2020/5/20 15:15
     * @Param   overDays
     * @Return  List<InvoiceNoticeVO>
     * @Exception
    */
    List<InvoiceNoticeVO> selectUploadFlowTimeoutOrder(Integer overDays);

    /**
     * @Description 超时未补传流水（或审核未通过）开票订单通知处理
     * @Author  Kaven
     * @Date   2020/5/21 9:36
     * @Param  invoiceOrder-开票订单 overDays
     * @Return
     * @Exception
    */
    void handleUploadFlowTimeoutNotice(InvoiceNoticeVO invocieOrder, Integer overDays);

    /**
     * @Description 根据用户ID更新开票订单表通知状态
     * @Author  Kaven
     * @Date   2020/5/21 10:02
     * @Param   userId  oemCode overDays
     * @Return
     * @Exception
    */
    void updateAlertStatus(Long userId, String oemCode, Integer overDays);

    /**
     * 根据企业Id统计历史累计开票金额
     * @param companyId
     * @return
     */
    Long sumOrderAmountByCompanyId(Long companyId);

    /**
     *获取开票达标的企业列表
     */
    List<Long> queryInvoiceReach(Long userId, String oemCode, Long minInvoiceAmount);

    /**
     *获取用户推广的个体的企业列表
     */
    List<Long> queryCompanyRegistReach(Long userId, String oemCode);

    /**
     * @Description 推广中心-业绩总览-直推用户列表查询
     * @Author  Kaven
     * @Date   2020/6/5 15:41
     * @Param  ExtendUserQuery
     * @Return  PageResultVo<ExtendUserVO>
     * @Exception
     */
    PageResultVo<ExtendUserVO> listDirectUsers(ExtendUserQuery query) throws BusinessException;

    /**
     * @Author yejian
     * @Description 推广中心-升为直客
     * @Date   2020/06/11 10:18
     * @param memberId
     * @param selectUserId
     * @return
     **/
    void upgradeToDirect(Long memberId, Long selectUserId) throws BusinessException;

    /**
     * 保存同时保存历史记录
     * @param invEntity
     * @param orderStatus
     * @param userAccount
     * @param hisRemark 历史表备注
     */
    void editAndSaveHistory(InvoiceOrderEntity invEntity, Integer orderStatus, String userAccount, String hisRemark);

    /**
     * 保存同时保存历史记录
     * @param invEntity 订单保存对象
     * @param histCopyEntity 历史记录保存对象
     * @param orderStatus
     * @param userAccount
     * @param hisRemark 历史表备注
     */
    void editAndSaveHistory(InvoiceOrderEntity invEntity, InvoiceOrderEntity histCopyEntity, Integer orderStatus, String userAccount, String hisRemark);

    /**
     * 修改开票订单状态
     * @param invEntity
     * @param orderEntity
     * @param userAccount
     * @param hisRemark 历史表备注
     */
    void updateInvoiceStatus(InvoiceOrderEntity invEntity, OrderEntity orderEntity, String userAccount, String hisRemark);

    /**
     * @Description 城市服务商佣金钱包提现时，创建提现开票订单
     * @Author  Kaven
     * @Date   2020/6/30 9:44 上午
     * @Param userWithdrawDto memberAccount
     * @Return string
     * @Exception  BusinessException
     */
    String createInvoiceOrderForCommission(UserWithdrawDTO userWithdrawDto, String memberAccount) throws BusinessException;

    /**
     * 接入方开票
     * @param dto
     * @param memberAccount
     * @return
     * @throws BusinessException
     */
    InvoiceOrderEntity createInvoiceOrderByThirdParty(ThirdPartyCreateInoiveIDTO dto, String memberAccount) throws BusinessException;


    /**
     * 查询增值税税率列表
     *
     * @return List<TaxRulesVatRateVO>
     */
    List<TaxRulesVatRateVO> getVatRateList(TaxRulesVatRateQuery query) throws BusinessException;

    /**
     * 查询增值税税率列表
     * @param orderNo 订单号
     * @param images 图片地址
     * @param type 1：银行流水，2：业务合同 3：发票图片
     * @param remark 备注
     * @param updateUser 操作人
     * @return
     */
    void editInvoiceOrderPic(String orderNo, String images, Integer type, String remark, String updateUser) throws BusinessException;

    /**
     * 上传风险承诺函
     * @param orderNo 订单号
     * @param images 图片地址
     * @param remark 备注
     * @param updateUser 操作人
     * @return
     */
    void uploadRiskCommitment(String orderNo, String images, String remark, String updateUser) throws BusinessException;

    // =====================================对外接口业务=====================================

    /**
     * 会员企业列表查询
     *
     * @param oemCode
     * @param query
     * @return List<CompanyListApitVO>
     */
    List<CompanyListApitVO> getCompanyListByQuery(String oemCode, CompanyListApiQuery query) throws BusinessException;

    /**
     * @Description 企业开票
     * @param oemCode
     * @param entity
     * @Return CompanyInvoiceApiVO
     */
    CompanyInvoiceApiVO companyInvoice(String oemCode, CompanyInvoiceApiDTO entity) throws BusinessException;

    /**
     * @Description 企业开票税费计算
     * @param oemCode
     * @param entity
     * @Return CompanyInvoiceTaxCalcApiVO
     */
    CompanyInvoiceTaxCalcApiVO invoiceTaxCalc(String oemCode, CompanyInvoiceTaxCalcApiDTO entity) throws BusinessException;

    /**
     * 开票订单补传流水
     *
     * @param entity
     * @return Integer
     */
    Integer patchBankWater(String oemCode, InvOrderBankWaterApiDTO entity);

    /**
     * 查询开票订单补传流水列表
     * @param oemCode
     * @param query
     * @return List<InvoiceWaterOrderApiVO>
     */
    List<InvoiceWaterOrderApiVO> getInvWaterOrderListByQuery(String oemCode, InvoiceWaterOrderApiQuery query);

    /**
     * @Description 更新开票订单剩余提现额度
     * @Author  Kaven
     * @Date   2020/9/9 14:41
     * @Param invoiceOrderNo withdrawalAmount flag:0-扣减 1-添加
     * @Return
     * @Exception
    */
    void updateRemainingWithdrawAmount(String invoiceOrderNo, Long withdrawalAmount, int flag) throws BusinessException;

    /**
     * 退快递费
     * @param invEntity
     * @param orderEntity
     * @param useraccount
     */
    void refundPostageFee(InvoiceOrderEntity invEntity, OrderEntity orderEntity, String useraccount);

    /**
     * 计算服务费
     * @param userId
     * @param oemCode
     * @param companyId
     * @param invoiceAmount
     * @return
     */
    InvoiceServiceFeeVO calcServiceFee(Long userId, String oemCode, Long companyId, Long invoiceAmount);

    /**
     * 根据订单编号查询电子发票
     * @param orderNo
     * @return
     */
    List<InvoiceDetailVO> getEleInvByOrderNo(String orderNo);
    /**
     * 根据集团订单号统计电票和纸票得企业
     */
    List<Map<String,Object>> sumByGroupOrderNo(String groupOrderNo);
    /**
     * 查询时间范围内得累计开票金额(根据增值税率分组)
     */
    List<Map<String,Object>> queryTotaLInvoiceAmountByVat(String start, String end, Long parkId, Integer invoiceType, Long companyId);
    /**
     * 查询税期范围内得累计开票金额(根据增值税率分组)
     */
    List<Map<String,Object>> queryTotaLInvoiceAmountByVatByTaxYear(Integer year, Integer seasonal, Long parkId, Integer invoiceType, Long companyId);

    /**
     * 查询税期范围内得累计开票金额(所得税率)
     */
    Long queryTotaLInvoiceAmountByIit(Integer year, Integer seasonal, Long parkId, Integer invoiceType, Long companyId);
    /**
     * 查询时间范围内得累计开票金额(附加税)
     */
    Long queryTotaLInvoiceAmountByFj(String start, String end, Long parkId, Integer invoiceType, Long companyId);
    /**
     * 根据累计开票金额计算应缴增值税
     */
    Long queryVatByTotaLInvoiceAmount(int year, int seasonal, List<Map<String, Object>> mapList, Long parkId, Integer companyType, Long companyId);
    /**
     * 根据累计开票金额计算应缴所得税
     * @param totaLInvoiceAmount 累计开票金额
     * @param parkId 园区id
     * @param companyType 公司类型
     * @param vatShouldTaxMoney 本期应缴增值费
     * @param taxBillYear 税期年
     * @return
     */
    Map<String ,Object> queryIitByTotaLInvoiceAmount(Long totaLInvoiceAmount, Long parkId, Integer companyType, Long vatShouldTaxMoney, int taxBillYear, Long companyId);
    /**
     * 根据累计开票金额计算应缴附加税
     */
    Long queryAdditionalByTotaLInvoiceAmount(Long totaLInvoiceAmount, Long parkId, Integer companyType,Integer taxpayerType, Long vatShouldAmount);

    /**
     * 周期内已开票金额
     * @param companyId
     * @param oemCode
     * @return
     */
    List<CountPeriodInvoiceAmountVO> countPeriodInvoiceAmount(Date start, Date end, Long companyId, String oemCode, Integer invoiceType);

    /**
     * 获取支付信息(V3.2更新)
     * @param currUserId
     * @param oemCode
     * @param orderNo
     * @param vatRate
     * @return
     */
    PayInformationVO getInvoicePayInfo(Long currUserId, String oemCode, String orderNo, BigDecimal vatRate);

    /**
     * 周期内历史已缴税费
     * @return
     */
    Long periodPaidTax(Date start, Date end, Long companyId, String oemCode, Integer type);

    /**
     * 本周期应缴增值税
     * @param invOrder
     * @param periodInvoiceAmountList
     * @param vatRate
     * @param company
     * @param isOverage 是否超过减免 （0：否 ，1：是）
     * @return
     */
    Long periodPayableVatFee(InvoiceOrderEntity invOrder, List<CountPeriodInvoiceAmountVO> periodInvoiceAmountList, BigDecimal vatRate, MemberCompanyEntity company, int isOverage);

    /**
     * 获取截止时间配置
     * @param companyId
     * @param invoiceWay
     * @return
     */
    InvoiceEndtimeVO getInvoiceEndtime(Long companyId, Integer invoiceWay);

    /**
     * 获取开票服务费明细
     * @param orderNo
     * @return
     */
    List<InvoiceServiceFeeDetailVO> getInvServiceFeeDetail(String orderNo);

    /**
     * 税费计算
     * @param companyId 企业id
     * @param type 适用类型 1-企业开票 2-企业税单 3-企业预税单
     * @param orderNo 订单编号（使用类型为1-企业开票时需要）
     * @param season 季度（定时任务需生成当前季度税单时传参,参数为0无效）
     * @param year 税期所属年（需要指定时传参）
     * @param calculationType 计算类型 0-所有 1-只计算已缴税费以及增值附加税 2-只计算所得税
     * @version V3.2
     */
    @Deprecated
    Map<String, Object> taxCalculation(Long companyId, int type, String orderNo, BigDecimal vatRate, int season, int year, int calculationType);

    /**
     * 税费计算 V4.0
     * @param entity
     * @return
     */
    Map<String, Object> taxCalculation(TaxCalculationVO entity);

    /**
     * 应缴增值税
     * @param list
     * @param type 适用类型 1-企业开票 2-企业税单 3-企业预税单
     * @param moreThan 增值税超过减免 0-否 1-是
     * @return
     */
    Long payableVatFee(List<CountPeriodInvoiceAmountVO> list, int type, InvoiceOrderEntity invoiceOrder, BigDecimal vatRate, int moreThan);

    /**
     * 封装gateway开票取消
     */
    void invoiceConfirmGateway(String orderNo,String remark,String updateUser);

    /**
     * 封装gateway开票成功
     * @param orderNo
     * @param paymentVoucher
     * @param remark
     */
    void invoiceConfirmGatewaySuccess(String orderNo,String paymentVoucher,String remark,String updateUser);

    /**
     * 封装gateway发票签收
     * @param entity
     * @param updateUser
     */
    void thirdPartyInvoiceComplete(OrderEntity entity,String updateUser, Long accessPartyId);
    /**
     * 获取开票支付信息
     * @param orderNo
     * @return
     */
    InvoicePayInfo queryInvoicePayInfo(String orderNo);
    /**
     * 接入方开票订单查询接口
     */
    ThirdPartyQueryInoiveInfoVO thirdPartyQueryInvoiceDetail(String orderNo, String externalOrderNo, String accessPartyCode);

    /**
     * 第三方开票-推送数据
     * @param orderNo
     * @param map
     */
    void accessPartyPush(String orderNo, String oemCode, Long accessPartyId, HashMap<String, Object> map);

    /**
     * 查看作废凭证
     * @param orderNo
     * @param oemCode
     * @param currUserId
     * @return
     */
    List<String> getCancellationVoucher(String orderNo, String oemCode, Long currUserId);

    /**
     * 发票作废/红冲
     * @param orderVoidInfo
     */
    Map<String,Object> cancellation(OrderVoidInfo orderVoidInfo,String userName);

    /**
     * 基于当前订单查询其他开票订单
     * @param companyId
     * @param afterTheTime 晚于当前时间
     * @param beforeTheTime 早于当前时间
     * @param serviceFeeRate 服务费费率
     * @return
     */
    List<String> findExistPendingInvOrder(Long companyId, Date afterTheTime, Date beforeTheTime, BigDecimal serviceFeeRate, String orderNo);

    /**
     * 查询企业在当前时间之后创建的订单
     * @param companyId
     * @param addTime
     * @return
     */
    List<InvoiceOrderEntity> findExistPendingOrder(List<Long> companyId,Date addTime,String groupOrderNo);

    /**
     * 重新提交开票订单
     * @param orderNo
     * @param currUserId
     * @return
     */
    String resubmit(String orderNo, Long currUserId);

    /**
     * 根据企业id获取佣金开票状态为待出款的订单
     * @param company
     * @return
     */
    List<String> getOrderNoByCreateWayAndCompanyId(Long company);

    /**
     * 根据企业类型园区id查询当季开票金额
     * @param companyId
     * @param parkId
     * @return
     */
    Long getquarterAmountByCompanyTypeAndParkId(Long companyId,Long parkId);

    /**
     * 税务监控查询
     * @param companyId
     * @param invoiceAmount
     * @return
     */
    Map<String, Object> taxMonitoringQuery(Long companyId, Long invoiceAmount);

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
    List<CountPeriodInvoiceAmountVO> queryCompanyInvoiceAmountByEin(Long companyId,  int type, Date start,
                                                                   Date end, Integer invoiceType,
                                                                   Integer isCurrentCycle, Integer isAcrossQuarter);

    /**
     * 开票订单财务审核
     * @param orderNo 订单号
     * @param auditResult 审核结果
     * @param receiptPaymentVoucher 收款凭证
     * @param errorRemark 失败原因
     */
    void invoicePaymentReview(String orderNo,Integer auditResult,String receiptPaymentVoucher,String errorRemark,String userName);

    /**
     * 校验待财务审核开票订单数据
     * @param companyId 企业id
     * @param updateTime 更新时间
     * @param isBefore 是否当前待财务审核之前的数据  1-当前订单之前 2-当前订单之后
     * @param orderNo 订单号
     * @return
     */
    Integer checkInvoicePaymentReview(Long companyId,Date updateTime,Integer isBefore,String orderNo);

    /**
     * 开票订单线下支付(收单主体)详情
     * @param oemCode
     * @return
     */
    Map<String, String> offlinePaymentDetail(String oemCode);

    /**
     * 开票订单上传线下打款凭证
     * @param orderNo
     * @param vouchers
     */
    void uploadPaymentVoucher(String orderNo, String vouchers);
}