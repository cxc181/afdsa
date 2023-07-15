package com.yuqian.itax.order.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.entity.dto.UserRechargeDTO;
import com.yuqian.itax.capital.entity.dto.UserWithdrawDTO;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.order.dao.OrderMapper;
import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.RegisterOrderEntity;
import com.yuqian.itax.order.entity.dto.AccessPartyRegisterOrderDTO;
import com.yuqian.itax.order.entity.dto.RegisterOrderDTO;
import com.yuqian.itax.order.entity.dto.ThirdPartyQueryInoiveInfoDTO;
import com.yuqian.itax.order.entity.dto.UpgradeOrderDTO;
import com.yuqian.itax.order.entity.query.*;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.user.entity.CompanyResoucesApplyRecordEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.MemberLevelEntity;
import com.yuqian.itax.user.entity.dto.CompResApplyRecordDTO;
import com.yuqian.itax.user.entity.dto.CompanyCancelApiDTO;
import com.yuqian.itax.user.entity.dto.CompanyCertReturnApiDTO;
import com.yuqian.itax.user.entity.dto.CompanyCertUseApiDTO;
import com.yuqian.itax.user.entity.po.UpdateMemberPhonePO;
import com.yuqian.itax.user.entity.query.MemberExtendQuery;
import com.yuqian.itax.user.entity.vo.CompanyCancelApiVO;
import com.yuqian.itax.user.entity.vo.CompanyResoucesApplyRecordExportVO;
import com.yuqian.itax.user.entity.vo.ExtendRecordDetailVO;
import com.yuqian.itax.user.entity.vo.MemberUpgradeRulesVO;
import com.yuqian.itax.util.entity.*;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单表service
 *
 * @Date: 2019年12月06日 11:34:46
 * @author 蒋匿
 */
public interface OrderService extends IBaseService<OrderEntity,OrderMapper> {
    /**
     * @Description 分页查询订单信息
     * @Author  Kaven
     * @Date   2019/12/6 16:35
     * @Param  query
     * @Return PageInfo
     */
    PageInfo<RegisterOrderVO> getOrderListPage(RegOrderQuery query);

    /**
     * 查询开户订单
     * @param query
     * @return
     */
    List<OpenOrderVO> listOpenOrder(OrderQuery query);

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
     * 分页查询开户订单
     * @param query
     * @return
     */
    PageInfo<OpenOrderVO> listPageOpenOrder(OrderQuery query);

    /**
     * 分页查询开票订单
     * @param query
     * @return
     */
    PageInfo<InvOrderVO> listPageInvOrder(OrderQuery query);

    /**
     * 查询开票订单批量下载列表
     * @param query
     * @return
     */
    List<BatchInvOrderExportVO> invBatchExportOrderList(OrderQuery query);

    /**
     * 分页查询会员升级订单
     * @param query
     * @return
     */
    PageInfo<MemberLvUpOrderVO> listPageMemberLvUpOrder(OrderQuery query);

    /**
     * @Description 查询用户待通知订单列表
     * @Author  Kaven
     * @Date   2019/12/9 14:47
     * @Param  userId
     * @Return List
     */
    List<OrderVO> queryNoticeOrderList(Long userId);

    /**
     * @Description 调用国金订单推送接口
     * @Author  wangkail
     * @Date   2021/5/7 10:47
     * @Param
     * @Return
     */
    void goWJOrderPush(String orderNo, Integer tradeType, Long memberId, String oemCode, Date completeTime);

    //  订单推送国金
    void orderPush(OrderQuery query);

    /**
     * @Description 根据订单号查询记录
     * @Author  Kaven
     * @Date   2019/12/9 16:38
     * @Param  orderNo
     * @Return OrderEntity
     */
    OrderEntity queryByOrderNo(String orderNo);

    /**
     * 订单领证确认
     * @param orderEntity
     * @param regEntity
     * @param taxPolicyVO
     * @param license
     * @param useraccount
     */
    void confirmCertificate(OrderEntity orderEntity, RegisterOrderEntity regEntity, TaxPolicyEntity taxPolicyVO, String license, String businessLicenseCopy, String useraccount,String ein);

    /**
     * @Description 创建工商注册订单
     * @Author  Kaven
     * @Date   2019/12/10 15:16
     * @Param  userId orderDto
     * @Return String
     * @Exception BusinessException
     */
    String createIndustryOrder(Long userId, RegisterOrderDTO orderDto) throws BusinessException;

    /**
     * 开户逻辑校验
     * @param orderDto
     * @param userId
     * @param type 校验类型 0-注册订单 1-注册预订单
     */
    void registerOrderPreHandler(RegisterOrderDTO orderDto, Long userId, int type);

    /**
     * @Description 上传电子签名或者认证视频
     * @Author Kaven
     * @Date  2019/12/11 10:52
     * @Param step当前步骤 1 电子签名 2 视频采集 3 补充资料
     * @Param fileUrl 文件路径
     * @Param orderNo 订单号
     * @Param versionCode 版本号
     * @Return Map<String,Object> resultMap 返回上传后的文件访问路径和客服经理电话
     * @Exception BusinessException
     */
    Map<String,Object> updateRegOrderFile(Long userId, String fileUrl, String orderNo, Integer step, String versionCode) throws BusinessException;

    /**
     * @Description 微信回调更新订单状态
     * @Param payNo 流水号
     * @Param upOrderNo 外部订单号
     * @Param payDate 支付时间
     * @Param code 返回结果码
     * @Param upStatusCode 上游返回状态
     * @Param message 返回描述
     * @Author  Kaven
     * @Date   2019/12/11 14:12
     */
    void updateOrderStatus(String payNo, String upOrderNo, Date payDate, String code, String upStatusCode, String message);

    /**
     * @Description 订单状态更新
     * @Author  Kaven
     * @Date   2019/12/12 15:23
     * @Param  userId orderNo orderStatus
     */
    void updateOrderStatus(String account, String orderNo, Integer orderStatus);

    /**
     * 批量更新订单推送状态
     *
     *
     */
    void batchUpdateOrderChannelPushState();

    /**
     * @Description 更新订单状态和外部订单号
     * @Author  Kaven
     * @Date   2020/9/9 11:42
     */
    void updateOrderStatusAndExternalOrderNo(String externalOrderNo, String orderNo, Integer orderStatus);

    /**
     * 订单取消
     * @param entity 订单实体
     * @param status 取消的订单状态
     * @param userAccount 操作人
     */
    String cancelOrder(OrderEntity entity, Integer status, String userAccount, boolean isApi) throws BusinessException;

    /**
     * 财务审核
     * @param entity
     * @param memEntity
     */
    void memberAudit(OrderEntity entity, MemberAccountEntity memEntity);

    /**
     * 统计出库中，待发货数量
     * @param query
     * @return
     */
    Map<String, Integer> sumInvOrder(OrderQuery query);

    /**
     * 开票订单批量出库
     * @param query
     * @param userAccount 操作人
     * @return
     */
    List<InvOrderBatchShipmentsVO> listInvBatchStock(OrderQuery query, String userAccount);

    /**
     * 修改开票订单状态
     * @param invEntity
     * @param entity
     * @param remark 备注
     */
    void updateInvOrderStatus(InvoiceOrderEntity invEntity, OrderEntity entity, String remark);

    /**
     * 修改开票订单状态以及添加开票信息记录
     * @param invEntity
     * @param entity
     * @param remark
     */
    void updateInvOrderStatusAndlogisticsInfo(InvoiceOrderEntity invEntity, OrderEntity entity, String remark,String logisticsInfo,Long userId,String updateUser);


    /**
     * @Description 用户提现
     * @Author  Kaven
     * @Date   2019/12/16 17:00
     * @Param  userId  oemCode verifyCode amount bankNumber withdrawType
     * @Exception BusinessException
     */
    String userWithdraw(UserWithdrawDTO dto) throws BusinessException;

    /**
     * 修改企业资源申请状态
     */
    void  updateCompangApplyStatusBatch(List<CompanyResoucesApplyRecordExportVO> list, String userAccount);

    /**
     * 修改企业资源申请状态
     */
    void  updateCompangApplyStatus(CompanyResoucesApplyRecordEntity companyResoucesApplyRecordEntity, OrderEntity orderEntity);

    /**
     * @Description 用户充值
     * @Author  Kaven
     * @Date   2019/12/16 19:35
     * @Param  dto
     * @Exception BusinessException
     */
    ResultVo userRecharge(UserRechargeDTO dto) throws BusinessException;

    /**
     * 查询会员是否已经升级
     * @param memberId 会员id
     * @param productType 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票    9-税务顾问 10-城市服务商
     * @return
     */
    List<OrderEntity> queryMemberLvUpOrder(Long memberId, Integer productType);

    /**
     * 会员升级
     * @param entity
     * @param memberLevelEntity
     * @param userBankCardEntity
     * @param useraccount
     * @param remark
     */
    OrderEntity memberUpgrade(MemberAccountEntity entity, MemberLevelEntity memberLevelEntity, UserBankCardEntity userBankCardEntity, String useraccount, String remark);

    /**
     * 查询账单明细列表
     * @param memberId
     * @param oemCode
     * @param query
     * @return
     */
    List<BillDetailVO> listBillDetail(Long memberId, String oemCode, BillDetailQuery query) throws BusinessException;

    /**
     * 统计账单收入和支出
     * @param memberId
     * @param oemCode
     * @param query
     * @return
     */
    BillIncomePayVO statisBillIncomePay(Long memberId, String oemCode, BillDetailQuery query);

    /**
     * 后台提现
     *
     * @param entity                   资金账户实体
     * @param member                   用户
     * @param cardEntity
     * @param amount                   提现金额（元）
     * @param withdrawType             提现类型 1-消费钱包提现 2-佣金钱包提现 3-佣金钱包城市服务商提现
     * @param commissionInvoiceOrderNo 佣金开票订单号
     */
    void adminWithdraw(UserCapitalAccountEntity entity, UserBankCardEntity cardEntity, Long amount, MemberAccountEntity member, Integer withdrawType,
                       String commissionInvoiceOrderNo, Integer sourceType) throws BusinessException;

    /**
     * 提现查询
     * @param orderEntity
     */
    void withdrawQuery(OrderEntity orderEntity) throws BusinessException;

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
     * @Description 构建微信支付参数
     * @Author  Kaven
     * @Date   2020/1/8 9:53
     * @Param  userId oemCode orderNo amount
     * @Return WechatPayDto
     */
    WechatPayDto buildWechatParams(Long userId, String oemCode, String orderNo, Long amount) throws BusinessException, UnknownHostException;

    /**
     * @Description 构建接入方支付参数
     * @Author  lmh
     * @Date   2021/8/13
     * @Param  userId oemCode orderNo amount
     * @Return WeChatPayH5Dto
     */
    AccessPartyPayDto buildAccessPartyParams(Long userId, String oemCode, String orderNo, Long amount, Integer sourceType, String appId) throws BusinessException, UnknownHostException;

    /**
     * @Description 构建微信支付参数
     * @Author  Kaven
     * @Date   2020/1/8 9:53
     * @Param  oemCode payNo refundOrderNo
     * @Return WechatPayDto
     */
    WechatRefundDto buildWechatRefundParams(String oemCode, String payNo ,String refundOrderNo) throws BusinessException, UnknownHostException;

    /**
     * @Description 构建字节跳动退款参数
     * @Author  Kaven
     * @Date   2020/1/8 9:53
     * @Param  payWaterEntity payNo
     * @Return WechatPayDto
     */
    BytedanceRefundDto buildBytedanceRefundParams(PayWaterEntity payWaterEntity, String payNo) throws BusinessException, UnknownHostException;

    /**
     * @param sourceType 操作小程序来源 1-微信小程序 2-支付宝小程序
     * @Description 创建企业注销订单
     * @Author Kaven
     * @Date 2020/2/14 9:19
     * @Param userId-用户ID  oemCode id-企业ID
     * @Return String
     * @Exception BusinessException
     */
    Map<String, Object> createComCancelOrder(Long userId, String oemCode, Long companyId, String sourceType) throws BusinessException;

    /**
     * 分页查询企业注销订单
     * @param query
     * @return
     */
    PageInfo<CompanyCancelOrderVO> listPageCancelOrder(OrderQuery query);

    /**
     * 查询企业注销订单
     * @param query
     * @return
     */
    List<CompanyCancelOrderVO> listCancelOrder(OrderQuery query);

    /**
     * @Description 查询指定用户和公司ID下所有不是已取消状态的企业注销订单
     * @Author  Kaven
     * @Date   2020/2/19 11:36
     * @Param  userId oemCode companyId
     * @Return list
     * @Exception
     */
    List<OrderEntity> queryComCancelOrder(Long userId, String oemCode, Long companyId, Integer orderStatus);

    /**
     * 退款
     * @param entity
     * @param userAccount
     * @param date
     * @param payChannels  退款通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗 8-字节跳动支付
     * @param allowRefunds 是否允许多笔退款
     * @throws BusinessException
     */
    void refund(OrderEntity entity, String userAccount, Date date,Integer payChannels, boolean allowRefunds) throws BusinessException;

    /**
     * @param memberId
     * @param oemCode
     * @param entity
     * @param sourceType 操作小程序来源 1-微信小程序 2-支付宝小程序
     * @Description 企业资源申请
     * @Return String
     */
    String certUseOrReturnOrder(Long memberId, String oemCode, CompResApplyRecordDTO entity, String sourceType) throws BusinessException;

    /**
     * 取消企业资源申请
     * @param memberId
     * @param oemCode
     * @param orderNo
     * @return
     */
    void cancelCertOrder(Long memberId, String oemCode, String orderNo) throws BusinessException;


    void cancelCertOrderAdmin(Long memberId, String oemCode, String orderNo) throws BusinessException;

    /**
     * 证件领用确认收货
     * @param memberId
     * @param oemCode
     * @param orderNo
     * @Return
     */
    void certUseConfirm(Long memberId, String oemCode, String orderNo) throws BusinessException;

    /**
     * 企业资源申请进度查询
     * @param memberId
     * @param orderNo
     * @Return Map
     */
    Map<String,Object> certOrderProgress(long memberId, String orderNo) throws BusinessException;

    /**
     * @Description 会员升级订单查询-分页-拓展宝
     * @Author  Kaven
     * @Date   2020/3/25 15:50
     * @Param   TZBOrderQuery
     * @Return  PageResultVo<MemberUpgradeOrderVO>
     * @Exception
     */
    PageResultVo<MemberUpgradeOrderVO> queryUpgradeOrder(TZBOrderQuery query);

    /**
     * 根据会员查询订单
     * @param memberId
     * @param oemCode
     * @param orderType
     * @param orderStatus
     * @return
     */
    OrderEntity queryByMemberId(Long memberId, String oemCode, Integer orderType, Integer orderStatus);

    /**
     * xxljob十分钟未支付取消企业资源申请
     * @param orderNo
     * @return
     */
    void xxljobCancelCertOrder(String orderNo);

    /**
     *下载办理信息列表
     */
    List<OpenOrderExportVO> getopenOrderExportList(OrderQuery query);


    /**
     * 统计会员日推广数据
     * @param orderEntity （type=1时候必传）
     * @param account 操作人
     * @param type  0-用户注册 1-订单完成
     * @return
     */
    void statisticsMemberGeneralize(OrderEntity orderEntity, String account, int type);


    /**
     * @Description 推广中心-推广记录明细查询
     * @Author  Kaven
     * @Date   2020/6/7 11:11 下午
     * @Param   MemberExtendQuery
     * @Return  ExtendRecordDetailVO
     * @Exception  BusinessException
     */
    ExtendRecordDetailVO queryExtendRecordDetail(MemberExtendQuery query) throws BusinessException;
    /**
     * @Description 推广中心-推广记录明细查询
     * @Author  Hz
     * @Date   2021/4/30 11:11 下午
     * @Param   MemberExtendQuery
     * @Return  ExtendRecordDetailVO
     * @Exception  BusinessException
     */
    ExtendRecordDetailVO queryExtendRecordDetailByChannelServiceId(MemberExtendQuery query) throws BusinessException;

    /**
     * 会员自动升级
     * @param userId
     */
    void memberAutoUpdate(Long userId);

    /**
     * @Description 佣金钱包提现
     * @Author  Kaven
     * @Date   2020/6/28 9:24 上午
     * @Param  UserCapitalAccountEntity UserBankCardEntity UserWithdrawDTO memberAccount
     * @Return String
     * @Exception  BusinessException
     */
    String userWithdrawForCommission(UserCapitalAccountEntity userCapitalAccount, UserBankCardEntity userBankCard, UserWithdrawDTO dto, String memberAccount) throws BusinessException;

    /**
     * @Description 创建佣金钱包提现订单
     * @Author  Kaven
     * @Date   2020/6/28 11:17 上午
     * @Param  UserWithdrawDTO UserBankCardEntity addUser levelNo
     * @Exception  BusinessException
     */
    OrderEntity createCommissionWithdrawOrder(UserWithdrawDTO dto, UserBankCardEntity cardEntity, MemberAccountEntity member, Integer levelNo, Integer payChannels) throws BusinessException;

    /**
     * @Description 处理佣金钱包提现结果
     * @Author  Kaven
     * @Date   2020/6/28 3:03 下午
     * @Param  userCapitalAccount-用户资金账户 order-订单信息 status-状态：0-提现成功 1-提现失败
     * @param resMsg 渠道失败原因
     * @Exception  BusinessException
     */
    void handleCommissionWithdrawResult(OrderEntity order, UserCapitalAccountEntity userCapitalAccount, int status, String resMsg) throws BusinessException;

    /**
     * @Description 计算提现到账金额
     * @Author  Kaven
     * @Date   2020/6/29 2:31 下午
     * @Param   currUserId oemCode orderAmount withdrawType
     * @Return  map
     * @Exception  BusinessException
     */
    Map<String, Object> calWithdrawReachAmount(Long currUserId, String oemCode, Long orderAmount, Integer withdrawType) throws BusinessException;

    /**
     * 代理提现
     * @param entity 资金账户
     * @param cardEntity 银行卡
     * @param amount 提现金额
     * @param addUser 操作人
     * @param remark 备注
     * @throws BusinessException
     */
    void agentAdminWithdraw(UserCapitalAccountEntity entity, UserBankCardEntity cardEntity, Long amount, String addUser, String remark) throws BusinessException;

    /**
     * 代理充值
     * @param entity
     * @param cardEntity
     * @param amount
     * @param addUser
     * @param remark
     * @throws BusinessException
     */
    void agentAdminRecharge(UserCapitalAccountEntity entity, UserBankCardEntity cardEntity, Long amount, String addUser, String remark) throws BusinessException;

    /**
     * @Description 创建企业注销订单
     * @Author  yejian
     * @Date   2020/07/17 9:19
     * @Param  oemCode
     * @Param  entity
     * @Return  CompanyCancelApiVO
     * @Exception BusinessException
     */
    CompanyCancelApiVO createComCancelOrder(String oemCode, CompanyCancelApiDTO entity) throws BusinessException;

    /**
     * 获取签名时间
     * @param orderEntity
     * @return
     */
    void getSignDate(OrderEntity orderEntity, JSONObject json) ;

    /**
     * @Description 用户注销
     * @Author  Kaven
     * @Date   2020/7/16 11:32
     * @Param   userId oemCode
     * @Return
     * @Exception  BusinessException
     */
    void userLogOff(Long userId, String oemCode) throws BusinessException;

    /**
     * @Description 企业证件申请
     * @param oemCode
     * @param entity
     * @Return String
     */
    String certUseOrder(String oemCode, CompanyCertUseApiDTO entity) throws BusinessException;

    /**
     * @param oemCode
     * @param entity
     * @Description 企业证件归还
     * @Return String
     * @Exception BusinessException
     */
    String certReturnOrder(String oemCode, CompanyCertReturnApiDTO entity) throws BusinessException;

    /**
     * @param memberId
     * @param oemCode
     * @param entity
     * @param sourceType 操作小程序来源 1-微信小程序 2-支付宝小程序
     * @Description 创建会员升级订单
     * @Return String
     * @Exception BusinessException
     */
    String createUpgradeOrder(Long memberId, String oemCode, UpgradeOrderDTO entity, String sourceType) throws BusinessException;

    /**
     * @param memberId
     * @param oemCode
     * @param upFlag   会费返还达标标记（0-直推个体数达标 1-累计直推开票金额达标）
     * @Description 会员升级费用返还
     * @Return
     * @Exception BusinessException
     */
    void upgradeReimbursement(Long memberId, String oemCode, Integer upFlag) throws BusinessException;

    /**
     * @Description 查询会员中心升级信息
     * @Author Kaven
     * @Date 2020/6/3 17:03
     * @Param oemCode currUserId
     * @Return List<MemberUpgradeRulesVO>
     * @Exception BusinessException
     */
    List<MemberUpgradeRulesVO> selectUpgradeInfo(String oemCode, Long currUserId) throws BusinessException;

    /**
     * @Description 对公户订单超时自动取消
     * @Author  Kaven
     * @Date   2020/10/16 14:06
     * @Param   OrderEntity
     * @Return
     * @Exception  BusinessException
    */
    void autoCancelWithdrawOrder(OrderEntity order) throws BusinessException;

    /**
     * @Description 查询指定时间前的待提交对公户提现订单列表
     * @Author  Kaven
     * @Date   2020/10/16 14:16
     * @Param   minutes
     * @Return  List<OrderEntity>
     * @Exception  BusinessException
    */
    List<OrderEntity> selectToSubmitWithdrawOrder(Integer minutes) throws BusinessException;

    /**
     * @Description 构建支付宝支付参数
     * @Author Kaven
     * @Date 2020/10/21 11:50
     * @Param oemCode payNo amount buyerId buyerLogonId
     * @Return AliPayDto
     * @Exception BusinessException
     */
    AliPayDto buildAliPayParams(String oemCode, String payNo, Long amount, String buyerId, String buyerLogonId) throws BusinessException, UnknownHostException;

    /**
     * 查询账单记录API
     *
     * @param query
     * @return
     */
    BillRecordVO listBillDetailApi(BillDetailApiQuery query) throws BusinessException;


    void cancelAdmin(Long companyId, String cancelCredentials, String account, Integer cancelType);

    /**
     * 根据产品查询订单
     * @param productId
     * @param oemCode
     * @param orderType
     * @param orderStatus
     * @return
     */
    List<OrderEntity> queryByProductId(Long productId, String oemCode, Integer orderType, Integer orderStatus);

    /**
     * 创建基础订单
     * @param oemCode
     * @param userId
     * @param sourceType 用户来源
     */
    OrderEntity createBaseOrder(String oemCode, Long userId, String sourceType);

    /**
     * 保存补税订单
     * @param oemCode
     * @param userId
     * @param sourceType
     * @param companyTaxBillEntity
     * @return
     */
    OrderEntity saveTaxSupplement(String oemCode, Long userId, String sourceType, CompanyTaxBillEntity companyTaxBillEntity);

    /**
     * 保存退税订单
     * @param oemCode
     * @param userId
     * @param sourceType
     * @param companyTaxBillEntity
     * @return
     */
    void saveTaxRefund(String oemCode, Long userId, String sourceType, CompanyTaxBillEntity companyTaxBillEntity);

    /**
     * 统计对公户提现重复金额订单
     * @param orderType
     * @param exceptOrderStatus
     * @param payAmount
     * @param repeatTime
     */
    Integer corporateAmoutRepeatCheck(Integer orderType, Integer exceptOrderStatus, Long payAmount, String repeatTime, Long corporateAccountId);

    /**
     * 修改会员手机号
     */
    void updateMemberPhone(MemberAccountEntity entity, UpdateMemberPhonePO po, String account);

    /**
     * 接入方开票查询开票信息
     */
    ThirdPartyQueryInoiveInfoDTO queryThirdPartyInvoiceInfo(ThirdPartyQueryInoiveInfoQuery query);

    /**
     * 创建工商注册订单前置校验
     * @param idCardNumber
     */
    void preCheckOfRegOrder(String idCardNumber);


    /**
     * 组装字节跳动支付参数
     * @param userId
     * @param oemCode
     * @param payNo
     * @param amount
     * @param orderNo
     * @return
             * @throws BusinessException
     * @throws UnknownHostException
     */
    BytedancePayDto buildBytedanceParams(Long userId, String oemCode, String payNo, Long amount,String orderNo) throws BusinessException, UnknownHostException;

    /**
     * 接入方接口创建注册订单
     * @param userId
     * @param orderDto
     * @return
     * @throws BusinessException
     */
    Map<String,Object> createRegisterOrder(Long userId, AccessPartyRegisterOrderDTO orderDto) throws BusinessException;

    /**
     * 校验是否可以作废/红冲
     * @param orderNo
     * @return
     */
    OrderVoidInfo checkToVoid(String orderNo);

    /**
     * 代理提现金额收入
     * @param entity
     * @param orderNo
     * @param orderType
     * @param amount
     * @param updateUser
     */
    void addFreezeBalance(UserCapitalAccountEntity entity, String orderNo, Integer orderType, Long amount, String updateUser);

    /**
     * 根据企业id查询在税单待处理、待付款的注销订单的订单号
     * @param companyId
     * @return
     */
    List<String> getOderNoByCompanyAndOrderStatus(Long companyId);

    /**
     * 根据企业id查询非取消状态注销订单
     * @param companyId
     * @return
     */
    List<String> getOderNoByCompany(Long companyId);

    /**
     * 查询消费提现免手续文案
     * @param oemCode
     * @return
     */
    String queryConsumptionWithdrawExplain(String oemCode);

    /**
     * 用户月累计消费钱包提现金额
     * @param memberId
     * @return
     */
    Long monthlyWithdrawalAmount(Long memberId, int year, int month);

    /**
     * 获取用户消费钱包可提现金额
     * @param userId
     * @return
     */
    Long usableWithdrawAmount(Long userId, String oemCode);

    /**
     * 查询未达充值天数的充值金额
     * @param userId
     * @param rechargeDays 充值天数
     * @return
     */
    Long queryRechargeAmountOfDays(Long userId, Integer rechargeDays);
}