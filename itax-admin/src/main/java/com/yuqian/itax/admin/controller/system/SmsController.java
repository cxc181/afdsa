package com.yuqian.itax.admin.controller.system;

import com.google.common.collect.Maps;
import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.capital.entity.UserCapitalAccountEntity;
import com.yuqian.itax.capital.enums.BankCardTypeEnum;
import com.yuqian.itax.capital.enums.CardStatusEnum;
import com.yuqian.itax.capital.service.UserBankCardService;
import com.yuqian.itax.capital.service.UserCapitalAccountService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.util.util.ActivationCode;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * 短信controller
 * @author：pengwei
 * @Date：2019/12/30 11:12
 * @version：1.0
 */
@RestController
@RequestMapping("sms")
@Slf4j
public class SmsController extends BaseController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserBankCardService userBankCardService;

    @Autowired
    private UserCapitalAccountService userCapitalAccountService;

    @ApiOperation("提现获取验证码")
    @PostMapping("withdraw")
    public ResultVo getVerifyCode(@JsonParam Long id) {
        //带登陆验证
        UserEntity userEntity = userService.findById(getCurrUserId());
        if (userEntity == null) {
            return ResultVo.Fail(ResultConstants.USER_NOT_EXISTS.getRetMsg());
        }
        UserCapitalAccountEntity entity = userCapitalAccountService.findById(id);
        if (entity == null) {
            return ResultVo.Fail("资金账户不存在");
        }
        if (!Objects.equals(entity.getUserType(), 2)) {
            throw new BusinessException("用户类型有误");
        }
        if (!Objects.equals(entity.getUserId(), userEntity.getId())) {
            throw new BusinessException("资金账户归属有误");
        }
//        if (userEntity.getPlatformType()==1 && Objects.equals(UserAccountTypeEnum.ADMIN.getValue(), userEntity.getAccountType())) {
//            if (!Objects.equals(entity.getUserType(), UserTypeEnum.PALTFORM.getValue())) {
//                return ResultVo.Fail("用户类型有误");
//            }
//        } else {
//            if (!Objects.equals(entity.getUserId(), userEntity.getId())) {
//                return ResultVo.Fail("资金账户归属有误");
//            }
//            if (Objects.equals(entity.getUserType(), UserTypeEnum.MEMBER.getValue())
//                    || Objects.equals(entity.getUserType(), UserTypeEnum.PALTFORM.getValue())) {
//                return ResultVo.Fail("用户类型有误");
//            }
//        }
        UserBankCardEntity cardEntity = new UserBankCardEntity();
        cardEntity.setUserId(entity.getUserId());
        cardEntity.setUserType(entity.getUserType());
        cardEntity.setStatus(CardStatusEnum.BIND.getValue());
        cardEntity.setOemCode(entity.getOemCode());
        cardEntity.setBankCardType(BankCardTypeEnum.DEBIT_CARD.getValue());
        cardEntity = userBankCardService.selectOne(cardEntity);
        if (cardEntity == null || StringUtils.isBlank(cardEntity.getBankNumber())) {
            return ResultVo.Fail("暂无绑定提现银行账户，请联系上级机构添加！");
        }
        if (StringUtils.isBlank(cardEntity.getPhone())) {
            return ResultVo.Fail("银行账户手机号为空");
        }
        Map<String, Object> resultMap;
        try {
            //获取验证码并发送
            String code = ActivationCode.getActivationCode();
            Map<String, Object> map = Maps.newHashMap();
            map.put("verficationCode", code);
            resultMap = smsService.sendTemplateSms(cardEntity.getPhone(), cardEntity.getOemCode(), VerifyCodeTypeEnum.WITHDRAW.getValue(), map,2);
            if (!"SUCCESS".equals(resultMap.get("code"))){
                return ResultVo.Fail((String)resultMap.get("message"));
            }
            redisService.set(RedisKey.SMS_WALLET_WITHDRAW_ADMIN_KEY_SUFFER + cardEntity.getOemCode() + "_" + cardEntity.getPhone(), code,300);//timeout秒为单位
            return ResultVo.Success();
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        }
    }
}