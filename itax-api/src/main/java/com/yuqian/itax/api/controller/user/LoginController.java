package com.yuqian.itax.api.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.api.annotation.JsonParam;
import com.yuqian.itax.api.annotation.lock.RedisLockAnnotation;
import com.yuqian.itax.api.annotation.lock.RedisLockTypeEnum;
import com.yuqian.itax.api.controller.BaseController;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.dto.MemberLoginDTO;
import com.yuqian.itax.user.entity.vo.GJLoginVO;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.util.util.guojin.GuoJinUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/9 10:30
 *  @Description: 登录（注册）控制器（小程序）
 */
@Api(tags = "登录（注册）控制器")
@RestController
@RequestMapping("/user")
@Slf4j
public class LoginController extends BaseController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OemParamsService oemParamsService;

    /**
     * @Description 会员登录（注册）接口
     * @Author  Kaven
     * @Date   2019/12/9 10:43
     * @Param  account 登录账号
     * verifyCode 验证码
     * inviterAccount 邀请人账号
     * @Return ResultVo
    */
    @ApiOperation("会员登录（注册）")
    @PostMapping("/login")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.LOGIN_REGISTER, lockTime = 10)
    public ResultVo login(@RequestBody @Validated MemberLoginDTO loginDto, BindingResult result){
        if(null == loginDto){
            return ResultVo.Fail("登录参数不能为空");
        }

        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        if (StringUtils.isBlank(loginDto.getAccount()) || StringUtils.isBlank(loginDto.getVerifyCode())) {
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        String oemCode = getRequestHeadParams("oemCode");
        loginDto.setOemCode(oemCode);
        String sourceType = this.getRequestHeadParams("sourceType");// 请求来源:支付宝or微信or其他
        if(StringUtil.isEmpty(sourceType)){
            sourceType = "1";// 默认来源微信小程序，向下兼容
        }
        if(!("1".equals(sourceType) || "2".equals(sourceType) || "3".equals(sourceType)|| "4".equals(sourceType))){
            return ResultVo.Fail("未知操作来源，请联系管理员");
        }
        if("2".equals(sourceType) && StringUtil.isBlank(loginDto.getAuthCode())){
            return ResultVo.Fail("authCode不能为空");
        }
        loginDto.setSourceType(Integer.parseInt(sourceType));
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap = this.memberAccountService.userLogin(loginDto);

        // 注册成功，统计会员日推广数据
        if("1".equals((String)dataMap.get("isRegFlag"))){
            OrderEntity order = new OrderEntity();
            order.setUserId((Long) dataMap.get("memberId"));
            order.setOemCode(oemCode);
            this.orderService.statisticsMemberGeneralize(order,loginDto.getAccount(),0);
        }
        return ResultVo.Success(dataMap);
    }

    /**
     * @Description 更新用户OpenId
     * @Author  jiangni
     * @Date   2019/12/9 10:43
     * @Param  account jsCode
     * @Return ResultVo
     */
    @ApiOperation("更新用户OpenId")
    @PostMapping("/updateOpenId")
    public ResultVo updateOpenId(@JsonParam String jsCode){
        if(StringUtils.isBlank(jsCode)){
            return ResultVo.Fail("jsCode不能为空");
        }
       CurrUser user = getCurrUser();
        memberAccountService.updateMemberWechatOpenId(user,jsCode);
        return ResultVo.Success();
    }

    /**
     * @Description 会员登出
     * @Author  Kaven
     * @Date   2019/12/9 13:59
    */
    @ApiOperation("会员登出")
    @PostMapping("/logout")
    public ResultVo logout(){
        String oemCode = getRequestHeadParams("oemCode");
        String token = getRequestHeadParams("token");
        redisService.delete(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + token);
        return ResultVo.Success("登出成功");
    }

    /**
     * 登录国金
     * @return
     */
    @ApiOperation("登录国金")
    @PostMapping("/loginToGJ")
    public ResultVo loginToGJ() {

        // 查询登录用户信息
        MemberAccountEntity member = Optional.ofNullable(memberAccountService.findById(getCurrUserId())).orElseThrow(() -> new BusinessException("未查询到用户信息"));

        // 查询机构配置
        OemParamsEntity paramEntity = oemParamsService.getParams(member.getOemCode(), 26);

        // 调用国金免登接口
        Map<String,Object> dataParams = new HashMap<>();
        dataParams.put("userId", member.getChannelUserId());
        dataParams.put("productCode", member.getChannelProductCode());
        dataParams.put("oemCode", member.getChannelCode());
        JSONObject jsonObject = GuoJinUtil.gjChannel(dataParams, member.getChannelCode(), paramEntity.getSecKey(), paramEntity.getUrl() + GuoJinUtil.LOGIN_TO_GJ);
        if (null == jsonObject.get("data") || !"0000".equals(jsonObject.getString("retCode"))) {
            throw new BusinessException(jsonObject.getString("retMsg"));
        }
        JSONObject data = jsonObject.getJSONObject("data");
        GJLoginVO gjLoginVO = data.toJavaObject(GJLoginVO.class);

        return ResultVo.Success(gjLoginVO);
    }
}