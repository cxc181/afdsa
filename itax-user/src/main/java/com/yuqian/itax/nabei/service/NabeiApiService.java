package com.yuqian.itax.nabei.service;

import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.nabei.entity.*;

import java.util.List;

/**
 * @ClassName: NabeiApiService
 * @Description: 纳呗平台接口service
 * @Author: yejian
 * @Date: Created in 2020/6/23
 * @Version: 1.0
 */
public interface NabeiApiService {

    /**
     * 签约注册
     * @param paramsEntity  机构参数配置
     * @param name          签约姓名
     * @param idcardNo      签约身份证号
     * @param accountNo     签约卡号
     * @param mobile        银行卡预留手机号码
     * @return APIRegisterRespVo
     */
    APIRegisterRespVo signRegister(OemParamsEntity paramsEntity, String name, String idcardNo, String accountNo, String mobile);

    /**
     * 签约注册查询
     * @param paramsEntity  机构参数配置
     * @param idcardNo      用户身份证号
     * @return APISignQueryRespVo
     */
    APISignQueryRespVo signQuery(OemParamsEntity paramsEntity, String idcardNo, String extAccountNo);

    /**
     * 修改签约账户
     * @param paramsEntity  机构参数配置
     * @param accountName   签约姓名
     * @param idcardNo      签约身份证号
     * @param orgAccountNo  原签约账户
     * @param accountNo     新签约账户
     * @param mobile        银行卡预留手机号码
     * @return ChangeBindCardRespVo
     */
    ChangeBindCardRespVo changeBindCard(OemParamsEntity paramsEntity, String accountName, String idcardNo, String orgAccountNo, String accountNo, String mobile);

    /**
     * 单笔出款申请
     * @param paramsEntity  机构参数配置
     * @param orderNo       出款订单号
     * @param accountName   签约姓名
     * @param idcardNo      签约身份证号
     * @param accountNo     签约卡号
     * @param mobile        预留手机号
     * @param amount        出款金额（单位元，保留两位小数）
     * @param remark        出款备注
     * @return SinglePayRespVo
     */
    SinglePayRespVo singlePay(OemParamsEntity paramsEntity, String orderNo, String accountName, String idcardNo, String accountNo, String mobile, String amount, String remark);

    /**
     * 单笔出款查询
     * @param paramsEntity  机构参数配置
     * @param orderNo       出款订单号
     * @return SinglePayQueryRespVo
     */
    SinglePayQueryRespVo singlePayQuery(OemParamsEntity paramsEntity, String orderNo);

    /**
     * 签约申请
     * @param paramsEntity
     * @param orderNo 签约订单号
     * @param accountName 客户姓名
     * @param idcardNo 客户身份证
     * @param accountType 签约账户类型 1-银行卡 2-支付宝 3-微信
     * @return
     */
    APIH5SignRespVo h5Sign(OemParamsEntity paramsEntity, String orderNo, String accountName, String idcardNo, int accountType, String accountNo);

    NabeiAPIBaseResp submitAchievements(OemParamsEntity paramsEntity, String realName, String idCardNo, String orderNo, String amount, List<AchievementExcelVo> profitsDetailList);
}
