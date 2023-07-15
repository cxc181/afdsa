package com.yuqian.itax.api.controller.system;

import com.google.common.collect.Maps;
import com.yuqian.itax.agent.entity.OemConfigEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemConfigService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.message.entity.MessageNoticeEntity;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.MessageNoticeService;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.dto.SmsVerifyDTO;
import com.yuqian.itax.util.util.ActivationCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 14:22
 *  @Description: 短信验证码/通知相关控制器
 */
@Api(tags = "短信验证码控制器")
@RestController
@RequestMapping("/system/sms")
@Slf4j
public class SmsController extends BaseController {
    @Autowired
    private SmsService smsService;
    @Autowired
    private MessageNoticeService messageNoticeService;
    @Autowired
    private OemService oemService;
    @Autowired
    private OemConfigService oemConfigService;

    /**
     * @Description 获取验证码
     * @Author  Kaven
     * @Date   2019/12/6 14:11
     * @Param  phone type
     * @Return ResultVo
    */
    @ApiOperation("获取验证码")
    @PostMapping("/verifyCode")
    public ResultVo getVerifyCode(@RequestBody @Valid SmsVerifyDTO dto, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        String oemCode = this.getRequestHeadParams("oemCode");
        OemEntity oemEntity =  oemService.getOem(oemCode);
        String verficationCode = null; //验证码
        Map<String, Object> resultMap = Maps.newHashMap();    //获取返回结果
        String templateType = dto.getType();// 短信模板，默认取验证码类型
        if (VerifyCodeTypeEnum.REGISTER.getValue().equals(dto.getType())) {// 注册操作
            // 判断手机号是否已经注册过
            MemberAccountEntity memberAccountEntity = memberAccountService.queryByAccount(dto.getPhone(), oemCode);
            // 查询机构配置信息，判断是否已接入渠道,已接入渠道机构不能注册新用户
            OemConfigEntity configEntity = Optional.ofNullable(oemConfigService.queryOemConfigByCode(oemCode, "is_open_channel")).orElseThrow(() -> new BusinessException("未查询到机构配置信息"));
            //判断手机号码是否已经注册，如果没有注册则返回前端一个标识，前端根据标识判断是否显示邀请人的输入框
            if (null == memberAccountEntity && Objects.equals(configEntity.getParamsValue(), "1")) {
                throw new BusinessException("该手机号未注册，请联系您的邀请人");
            } else if (null == memberAccountEntity && Objects.equals(configEntity.getParamsValue(), "0")) {
                // 手机号码未注册，短信模板使用注册模板
                templateType = "1";
                resultMap.put("isReg", false);// 返回前端标识
            } else if (null != memberAccountEntity) {
                // 否则使用登录模板
                templateType = "2";
            }
        }

        if(oemEntity != null && StringUtils.isNotBlank(oemEntity.getDefaultSmsCode())){
            verficationCode=oemEntity.getDefaultSmsCode();
        }else{
            verficationCode= ActivationCode.getActivationCode();
        }

        Map<String, Object> map = new HashMap();
        map.put("verficationCode", verficationCode);

        Map<String, Object> sendMap = smsService.sendTemplateSms(dto.getPhone(), oemCode, templateType, map, 1);
        resultMap.putAll(sendMap);// 合并结果集
        if (!"SUCCESS".equals(resultMap.get("code"))) {
            return ResultVo.Fail((String) resultMap.get("message"));
        }

        // 设置redis缓存
        if(VerifyCodeTypeEnum.REGISTER.getValue().equals(dto.getType())) {// 注册操作
            redisService.set(RedisKey.SMS_USER_LOGIN_KEY_SUFFER + dto.getPhone(),verficationCode,300);//timeout秒为单位
        }
        if(VerifyCodeTypeEnum.BIND_CARD.getValue().equals(dto.getType())) {// 用户绑卡
            redisService.set(RedisKey.SMS_USER_BIND_CARD_KEY_SUFFER + dto.getPhone(),verficationCode,300);//timeout秒为单位
        }
        if(VerifyCodeTypeEnum.UNBIND_CARD.getValue().equals(dto.getType())) {// 用户解绑
            redisService.set(RedisKey.SMS_USER_UN_BIND_CARD_KEY_SUFFER + dto.getPhone(),verficationCode,300);//timeout秒为单位
        }
        if(VerifyCodeTypeEnum.WITHDRAW.getValue().equals(dto.getType())) {// 用户提现
            redisService.set(RedisKey.SMS_WALLET_WITHDRAW_KEY_SUFFER + dto.getPhone(),verficationCode,300);//timeout秒为单位
        }
        if(VerifyCodeTypeEnum.BALANCE_PAY.getValue().equals(dto.getType())) {// 用户开票订单余额支付
            redisService.set(RedisKey.SMS_WALLET_BALANCE_PAY_KEY_SUFFER + dto.getPhone(),verficationCode,300);//timeout秒为单位
        }
        if(VerifyCodeTypeEnum.COMPANY_CORPORATE_ACCOUNT_WITHDRAW.getValue().equals(dto.getType())) {// 对公户提现
            redisService.set(RedisKey.SMS_CORP_ACCOUNT_WITHDRAW_KEY_SUFFER + dto.getPhone(),verficationCode,300);//timeout秒为单位
        }
        resultMap.remove("code");
        resultMap.remove("message");
        return ResultVo.Success(resultMap);
    }

    /**
     *  用户通知
     * @Author  蒋匿
     * @Date   2019/12/9 14:32
     * @Return ResultVo
    */
    @ApiOperation("用户通知")
    @PostMapping("/noticeUser")
    public ResultVo noticeUser(){
        // 查询当前登录用户下的所有待通知订单
        List<MessageNoticeEntity> messageNoticeEntityList = messageNoticeService.findAllHomeNotAlertMessageByUserId(getCurrUserId(), getRequestHeadParams("oemCode"));
        return ResultVo.Success(messageNoticeEntityList);
    }

    /**
     * @Description 更新用户通知状态
     * @Author  蒋匿
     * @Date   2019/12/9 14:32
     * @param  id 通知id
     * @param status 是否已读 0-未读 1-已读
     * @Return ResultVo
     */
    @ApiOperation("更新用户通知状态")
    @PostMapping("/updateNoticeCount")
    @ApiImplicitParam(name="id",value="通知id",dataType="Long",required = true)
    public ResultVo updateNoticeCount(@JsonParam Long id,@JsonParam Integer status){
        if(null == id){
            return ResultVo.Fail("通知id不能为空!");
        }
        MessageNoticeEntity entity = new MessageNoticeEntity();
        entity.setUserId(getCurrUserId());
        entity.setUserType(1);
        entity.setStatus(0);
        entity.setId(id);
        entity = messageNoticeService.selectOne(entity);
        if(entity == null){
            return ResultVo.Fail("未找到相关的通知信息");
        }

        // 查询当前登录用户下的所有待通知订单
        entity = new MessageNoticeEntity();
        entity.setUserId(getCurrUserId());
        entity.setUserType(1);
        entity.setIsAlert(1);
        if(status == null || status == 0){
            entity.setStatus(0);
        }else {
            entity.setStatus(1);
        }
        entity.setId(id);
        entity.setUpdateUser(getCurrUser().getUseraccount());
        int result = messageNoticeService.updateNoticeById(entity);
        if(result < 1){
            return ResultVo.Fail("通知更新失败！");
        }
        return ResultVo.Success();
    }
}