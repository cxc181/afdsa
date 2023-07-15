package com.yuqian.itax.order.service;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.service.IBaseService;
import com.yuqian.itax.common.base.vo.PageResultVo;
import com.yuqian.itax.order.dao.RegisterOrderMapper;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.entity.RegisterOrderEntity;
import com.yuqian.itax.order.entity.dto.*;
import com.yuqian.itax.order.entity.query.AccessPartyOrderQuery;
import com.yuqian.itax.order.entity.query.TZBOrderQuery;
import com.yuqian.itax.order.entity.vo.*;
import com.yuqian.itax.pay.entity.PayWaterEntity;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.util.entity.AliPayDto;
import com.yuqian.itax.util.entity.WechatPayDto;
import com.yuqian.itax.util.entity.WechatRefundDto;
import org.apache.ibatis.annotations.Param;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * 工商注册订单service
 * 
 * @Date: 2019年12月07日 20:06:37 
 * @author 蒋匿
 */
public interface RegisterOrderService extends IBaseService<RegisterOrderEntity,RegisterOrderMapper> {
    /**
     * @Description 根据类型更新签名地址/短视频地址
     * @Author  Kaven
     * @Date   2019/12/11 14:07
     * @Param  orderNo  url type
    */
    void updateSignOrVideoAddr(String orderNo, String url, int type);

    /**
     * @Description 订单支付
     * @Author  Kaven
     * @Date   2019/12/12 10:33
     * @Param  payDto
     * @Return JSONObject
    */
    JSONObject orderPay(RegOrderPayDTO payDto) throws BusinessException, UnknownHostException;

    /**
     * @Description 工商注册/会员升级/用户充值订单取消
     * @Author  Kaven
     * @Date   2020/1/9 10:01
     * @Param  userId orderNo
     * @Exception BusinessException
    */
    void cancelOrder(Long userId, String orderNo) throws BusinessException;

    /**
     * @Description 订单支付金额为0时处理方法
     * @Author  Kaven
     * @Date   2020/1/14 20:22
     * @Param  payDto prodName
     * @Return  JSONObject
     * @Exception
    */
    JSONObject payZeroAmountOrder(RegOrderPayDTO payDto,String prodName) throws BusinessException;

    /**
     * @Description 更新订单客服和经营地址
     * @Author  Kaven
     * @Date   2020/2/12 17:33
     * @Param  orderNo customerServicePhone businessAddr isOpenAuthentication 是否已开启身份验证 0-未开启 1-已开启
     * @Return
     * @Exception
    */
    void updateCusomerPhoneAndBusinessAddr(String orderNo, String customerServicePhone, String businessAddr,Integer isOpenAuthentication);

    /**
     * @Description 构建渠道端微信订单查询参数
     * @Author  Kaven
     * @Date   2020/3/3 16:56
     * @Param payWater
     * @Return WechatPayDto
     * @Exception
     */
    WechatPayDto buildWechatParams(PayWaterEntity payWater) throws BusinessException;


    /**
     * @Description 构建渠道端微信退款订单查询参数
     * @Author  HZ
     * @Date   2021/8/16 16:56
     * @Param payWater
     * @Return WechatPayDto
     * @Exception
     */
    WechatRefundDto buildRefundWechatParams(PayWaterEntity payWater) throws BusinessException;
    /**
     * @Description 开户订单支付成功订单处理
     * @Author  Kaven
     * @Date   2020/3/17 12:19
     * @Param  order userId orderStatus
     * @Return
     * @Exception BusinessException
    */
    void successPayHandle(OrderEntity order, Long userId,Integer orderStatus) throws BusinessException;

    /**
     * @Description 订单支付预处理，查询实时订单状态，并给出相应处理
     * @Author  Kaven
     * @Date   2020/3/20 9:22
     * @Param   OrderEntity MemberAccountEntity
     * @Return   JSONObject
     * @Exception  BusinessException, UnknownHostException
    */
    JSONObject orderCheckBeforePay(OrderEntity order, MemberAccountEntity member) throws BusinessException;

    /**
     * @Description 会员升级订单支付成功处理（兼容支付金额为0时的情况：支付金额为0时操作类型为1，否则为2）
     * @Author  Kaven
     * @Date   2020/3/20 10:27
     * @Param   productId,prodName,oemCode,orderNo,userId,operateType:操作类型：1-新增 2-更新
     * @Return  OrderEntity
     * @Exception BusinessException
    */
    OrderEntity memberUpgradePaySuccess(Long productId, String prodName,String oemCode, String orderNo, Long userId,Integer operateType) throws BusinessException;

    /**
     * @Description 企业注册订单查询-分页-拓展宝
     * @Author  Kaven
     * @Date   2020/3/25 14:38
     * @Param   TZBOrderQuery
     * @Return  PageResultVo<RegOrderVO>
     * @Exception
     */
    PageResultVo<RegOrderVO> queryRegOrderPage(TZBOrderQuery query);

    /**
     * @Description 重新提交企业注册订单
     * @Author  Kaven
     * @Date   2020/6/3 14:58
     * @Param   userId entity
     * @Return
     * @Exception  BusinessException
    */
    void resubmitRegOrder(Long userId, ResubmitRegOrderDTO entity) throws BusinessException;

    /**
     * @Description 查询字号
     * @Author  Kaven
     * @Date   2020/6/3 16:22
     * @Param   orderNo
     * @Return  Map<String, Object>
     * @Exception
    */
    Map<String, Object> queryShopName(String orderNo, Long userId) throws BusinessException;

    /**
     * @Description 根据ID更新订单数据（因字号更新的特殊性，不能使用editByIdSelective方法）
     * @Author  Kaven
     * @Date   2020/6/11 4:44 下午
     * @Param   RegisterOrderEntity
     * @Return
     * @Exception
    */
    void updateOrderById(RegisterOrderEntity tt);

    /**
     * @Description 查询待身份验证的订单列表
     * @Author  yejian
     * @Date   2020/6/11 18:22
     * @Param   memberId
     * @Param   oemCode
     * @Return  List<String>
     */
    List<String> queryTobeAuthRegOrder(Long memberId, String oemCode);

    /**
     * @Description 更新待身份验证订单的通知次数
     * @Author  yejian
     * @Date   2020/6/11 18:22
     * @Param   memberId
     * @Param   orderNo
     */
    void setOrderAlertNum(Long memberId, String orderNo) throws BusinessException;

    /**
     * @Description 根据订单号修改订单状态并进行自动派单
     * @Author  Kaven
     * @Date   2020/6/12 11:01 上午
     * @Param  orderNo userId oemCode
     * @Exception  BusinessException
    */
    void ensureValidate(String orderNo,Long userId, String oemCode) throws BusinessException;

    /**
     * @Description 根据订单号更新是否微信授权标识
     * @Author  Kaven
     * @Date   2020/6/12 11:48 上午
     * @Param   orderNo flag userId oemCode
     * @Return
     * @Exception  BusinessException
    */
    void updateWechatAuthFlagByOrderNo(List<OrderWechatAuthRelaDTO> lists, Long userId, String oemCode) throws BusinessException;

    /**
     * 根据订单号获取支付信息
     * @param orderNo
     * @return
     */
    InvPayInfoVo queryPayInfoByOrderNo(String orderNo);
    /**
     * @Description 创建企业注册订单（含用户注册，外部调用）
     * @Author  Kaven
     * @Date   2020/7/15 15:08
     * @Param   OuterRegOrderDTO
     * @Return  RegOrderReturnVO
     * @Exception  BusinessException
    */
    RegOrderReturnVO createRegOrderForOuter(OuterRegOrderDTO entity) throws BusinessException, IOException;

    /**
     * @Description 创建工商注册订单（纯API调用）
     * @Author  Kaven
     * @Date   2020/7/17 14:25
     * @Param   MemberAccountEntity OuterRegOrderDTO
     * @Return  String
     * @Exception  BusinessException
    */
    String createRegOrder(MemberAccountEntity member, OuterRegOrderDTO entity) throws BusinessException;

    /**
     * @Description 文件上传（图片、视频）
     * @Author  Kaven
     * @Date   2020/7/20 10:28
     * @Param   FileUploadDTO oemCode
     * @Return   Map<String, Object>
     * @Exception  BusinessException
    */
    Map<String, Object> fileUpload(FileUploadDTO uploadDto, String oemCode) throws BusinessException, IOException;

    /**
     * 编辑并保存历史记录
     * @param entity
     * @param orderStatus
     * @param userAccount
     * @param hisRemark
     */
    void editAndSaveHistory(RegisterOrderEntity entity, Integer orderStatus, String userAccount, String hisRemark);

    /**
     * @Description 工商注册-补充资料（外部调用）
     * @Author  Kaven
     * @Date   2020/7/21 17:00
     * @Param   SupplyMeterialDTO
     * @Return  Map<String, Object>
     * @Exception  BusinessException
    */
    void supplyMaterials(SupplyMeterialDTO smDto) throws BusinessException;

    /**
     * @Description 构建支付宝订单查询请求参数
     * @Author  Kaven
     * @Date   2020/10/23 14:06
     * @Param   payWater
     * @Return  AliPayDto
     * @Exception  BusinessException
    */
    AliPayDto buildAliPayParams(PayWaterEntity payWater) throws BusinessException;

    /**
     * 确认登记
     * @param orderEntity
     * @param useraccount
     */
    void openRegisterConfirm(OrderEntity orderEntity, String useraccount);

    /**
     * 提交签名
     * @param orderEntity
     * @param orderStatus
     * @param useraccount
     */
    void openSubmitSign(OrderEntity orderEntity, Integer orderStatus, String useraccount);

    /**
     * 确认已申报
     * @param orderEntity
     * @param orderStatus
     * @param remark
     * @param useraccount
     */
    void openDeclareConfirm(OrderEntity orderEntity, Integer orderStatus, String remark, String useraccount);

    /**
     * 根据用户id查询注册订单列表
     * @param query
     * @return
     */
    List<RegisterOrderOfAccessPartyVO> listByMemberId(AccessPartyOrderQuery query);

    /**
     * 支付金额原路退回
     * @param oemCode
     * @param orderNo
     * @return
     * @throws UnknownHostException
     * @throws BusinessException
     */
    public Map<String, Object> routeAndRefundOrder(String oemCode ,String orderNo,String payNo,String account) throws UnknownHostException,BusinessException ;

    /**
     * 支付退款原路退回
     * @param oemCode
     * @param orderNo
     * @param payNo
     * @param account
     * @param payway 1-微信 5-字节跳动
     */
    public void routeAndRefundOrderReplay(String oemCode ,String orderNo,String payNo,String account,int payway) ;

    /**
     * 根据订单编号查询注册订单
     * @param orderNo
     * @return
     */
    RegisterOrderEntity queryByOrderNo(String orderNo);

    /**
     * 更新注册订单
     * @param registerOrderEntity
     */
    void updateByPrimaryKeySelective(RegisterOrderEntity registerOrderEntity);

    /**
     * 根据园区id获取最后一个使用的经营地址
     * @return
     */
    String getBusinessAddress(Long parkId);

    /**
     * 接入方支付添加支付凭证
     * @param dto
     */
    void addPaymentVoucher(ThirdPartyAddPaymentVoucherDTO dto);

    /**
     * 根据经营范围和园区id查询数据
     * @param content
     * @param parkId
     * @return
     */
    List<RegisterOrderEntity> getRegisterOrderByBusinessContent(@Param("content") String content, @Param("parkId") Long parkId);

    /**
     * 同步企业注册订单商品编码
     * @param dto
     */
    void synchronousTaxCode(SynchronousTaxCodeDTO dto, String accessPartyCode);

    /**
     * 取消未支付注册订单
     * @param orderNo
     */
    void cancelUnpaidOrder(String orderNo, String oemCode, Long memberId);

    /**
     * 新增/编辑注册订单
     * @param dto
     * @return
     */
    String addOrUpdateRegOrder(AddOrUpdateRegOrderDTO dto);

    /**
     * 根据注册订单获取企业信息数据
     * @param orderNo
     * @return
     */
    CompanyInfoOfRegOrderVO getCompanyInfo(String orderNo);
}

