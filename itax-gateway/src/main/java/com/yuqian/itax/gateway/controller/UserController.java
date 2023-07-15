package com.yuqian.itax.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.error.service.ErrorInfoService;
import com.yuqian.itax.gateway.annotation.JsonParam;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.order.entity.OrderEntity;
import com.yuqian.itax.order.service.OrderService;
import com.yuqian.itax.profits.entity.query.MemberProfitsGateWayQuery;
import com.yuqian.itax.profits.entity.vo.MemberOrUserProfitsVO;
import com.yuqian.itax.profits.service.ProfitsDetailService;
import com.yuqian.itax.user.entity.MemberAccountEntity;
import com.yuqian.itax.user.entity.dto.ExtUserAuthDTO;
import com.yuqian.itax.user.entity.dto.MemberBaseInfoApiDTO;
import com.yuqian.itax.user.entity.dto.MemberRegisterDTO;
import com.yuqian.itax.user.entity.query.MemberExtendQuery;
import com.yuqian.itax.user.entity.vo.*;
import com.yuqian.itax.user.service.MemberAccountService;
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

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * @Author: Kaven
 * @Date: 2020/5/13 9:11
 * @Description: 用户接口控制器
 */
@Api(tags = "用户服务API")
@RestController
@RequestMapping("/external/user")
@Slf4j
public class UserController extends BaseController {
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private ErrorInfoService errorInfoService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MemberAccountService accountService;
    @Autowired
    private ProfitsDetailService profitsDetailService;

    /**
     * @Description 用户注册
     * @Author Kaven
     * @Date 2020/5/13 9:12
     * @Param
     * @Return
     * @Exception
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public ResultVo register(@JsonParam String mobile){
        log.info("收到第三方用户注册请求：{}",mobile);

        if (StringUtils.isBlank(mobile)) {
            return ResultVo.Fail("注册手机号不能为空！");
        }
        Map<String,Object> dataMap = new HashMap<String,Object>();
        String oemCode = getRequestHeadParams("oemCode");
        try {
            MemberAccountEntity member = memberAccountService.extUserRegister(mobile, oemCode);
            dataMap.put("userId",member.getId());
        } catch (BusinessException e) {
            log.error("会员登录（注册）业务异常：", e);
            errorInfoService.addErrorInfo(1, "对外用户接口控制器", "用户注册", e.toString(), mobile, oemCode, mobile);
            return ResultVo.Fail(null == e.getErrorCode() ? ResultConstants.FAIL.getRetCode() : e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            log.error("系统未知异常：", e);
            errorInfoService.addErrorInfo(1, "对外用户接口控制器", "用户注册", e.toString(), mobile, oemCode, mobile);
            return ResultVo.Fail(MessageEnum.SYSTEM_ERROR.getMessage());
        }
        return ResultVo.Success(dataMap);
    }

    /**
     * @Description 第三方用户实名认证（二要素验证）
     * @Author  Kaven
     * @Date   2020/5/13 11:16
     * @Param userName idCardNo idCardFront idCardBack expireDate
     * @Return ResultVo
     * @Exception BusinessException
     */
    @ApiOperation("第三方用户实名认证（二要素验证）")
    @PostMapping("/userAuth")
    public ResultVo userAuth(@RequestBody @Valid ExtUserAuthDTO dto, BindingResult results){
        if(results.hasErrors()){
            return ResultVo.Fail(results);
        }
        String oemCode = getRequestHeadParams("oemCode");
        try{
            memberAccountService.extUserAuth(oemCode, dto.getMobile(), dto.getUserName(), dto.getIdCardNo(), dto.getIdCardFront(), dto.getIdCardBack());
        } catch (BusinessException e){
            log.error("第三方用户实名认证（二要素验证）业务异常{}",e.getMessage());
            errorInfoService.addErrorInfo(1, "对外用户接口控制器", "第三方用户实名认证（二要素验证）", e.toString(), JSONObject.toJSONString(dto), oemCode, dto.getMobile());
            return ResultVo.Fail(e.getMessage());
        }catch (Exception e){
            log.error("系统未知异常{}",e.getMessage());
            errorInfoService.addErrorInfo(1, "对外用户接口控制器", "第三方用户实名认证（二要素验证）", e.toString(), JSONObject.toJSONString(dto), oemCode, dto.getMobile());
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success();
    }

    /**
     * @Description 会员注册同步（云租）
     * @Author  Kaven
     * @Date   2020/6/23 3:29 下午
     * @Param
     * @Return
     * @Exception
     */
    @ApiOperation("会员注册同步（云租）")
    @PostMapping("/yunzu/register")
    public ResultVo register_yz(@RequestBody @Validated MemberRegisterDTO registerDto, BindingResult result){
        if(null == registerDto){
            return ResultVo.Fail("参数对象不能为空");
        }

        if(result.hasErrors()){
            return ResultVo.Fail(result);
        }

        String oemCode = getRequestHeadParams("oemCode");
        if(StringUtils.isBlank(oemCode)){
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        registerDto.setOemCode(oemCode);
        Map<String,Object> dataMap = new HashMap<>();
        try {
            dataMap = memberAccountService.userRegister(registerDto);
        } catch (IOException e) {
            log.info("身份证图片转码失败");
            dataMap.put("msg","身份证图片转码失败");
        }

        // 注册成功，统计会员日推广数据
        if("1".equals((String)dataMap.get("isRegFlag"))){
            OrderEntity order = new OrderEntity();
            order.setUserId((Long) dataMap.get("memberId"));
            order.setOemCode(oemCode);
            orderService.statisticsMemberGeneralize(order, registerDto.getMobile(), 0);
        }
        return ResultVo.Success(dataMap);
    }

    /**
     * @Description 获取用户登录token
     * @Param
     * @Return
     * @Exception
     */
    @ApiOperation("获取用户登录token")
    @PostMapping("/yunzu/getToken")
    public ResultVo getToken(@JsonParam String mobile,@JsonParam String idCardNo){
        log.info("收到第三方用户注册请求：mobile:{},idCardNo:{}",mobile,idCardNo);

        if (StringUtils.isBlank(mobile)) {
            return ResultVo.Fail("注册手机号不能为空！");
        }
        if (StringUtils.isBlank(idCardNo)) {
            return ResultVo.Fail("身份证号码不能为空！");
        }
        Map<String,Object> dataMap = new HashMap<String,Object>();
        String oemCode = getRequestHeadParams("oemCode");
        try {
            MemberAccountEntity memberAccountEntity = new MemberAccountEntity();
            memberAccountEntity.setOemCode(oemCode);
            memberAccountEntity.setMemberAccount(mobile);
            memberAccountEntity.setIdCardNo(idCardNo);
            memberAccountEntity.setStatus(1);
            MemberAccountEntity member = memberAccountService.selectOne(memberAccountEntity);
            if(member!=null){
                String outTime = sysDictionaryService.getByCode("redis_token_outtime").getDictValue();
                String token = UUID.randomUUID().toString().replaceAll("-","");
                redisService.set(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + "userId_1_" + member.getId(),token,Integer.parseInt(outTime));
                redisService.set(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + token,new CurrUser(member.getId(),mobile,oemCode,null),Integer.parseInt(outTime));

                dataMap.put("token",token);
                return ResultVo.Success(dataMap);
            }else{
                return ResultVo.Fail("获取token失败，未找到合适的用户数据！");
            }
        } catch (Exception e) {
            log.error("系统未知异常：", e);
            errorInfoService.addErrorInfo(1, "对外用户接口控制器", "获取用户登录token", e.toString(), mobile, oemCode, mobile);
            return ResultVo.Fail(MessageEnum.SYSTEM_ERROR.getMessage());
        }
    }

    /**
     * @Description 用户注销
     * @Author  Kaven
     * @Date   2020/7/15 14:46
     * @Param
     * @Return
     * @Exception
     */
    @ApiOperation("用户注销")
    @PostMapping("userLogOff")
    public ResultVo userLogOff(@JsonParam Long userId){
        log.info("收到用户注销请求：{}", userId);

        if (null == userId) {
            return ResultVo.Fail("操作失败，用户ID不能为空！");
        }
        String oemCode = getRequestHeadParams("oemCode");
        orderService.userLogOff(userId, oemCode);
        return ResultVo.Success();
    }

    /**
     * @Description 推广中心-业绩总览查询
     * @Author yejian
     * @Date 2020/11/18 14:55
     * @Param userId    会员id
     * @Param levelNo   会员等级 0-税务顾问 1-城市服务商 2-城市合伙人 3-高级城市合伙人
     * @Return ResultVo<AchievementStatVO>
     */
    @ApiOperation("推广中心-业绩总览查询")
    @PostMapping("queryAchievement")
    public ResultVo<AchievementStatVO> queryAchievement(@JsonParam Long userId, @JsonParam Integer levelNo) {
        if (null == userId) {
            return ResultVo.Fail("操作失败，用户ID不能为空！");
        }
        if (null == levelNo) {
            return ResultVo.Fail("操作失败，会员等级不能为空！");
        }
        String oemCode = getRequestHeadParams("oemCode");
        AchievementStatVO statVO = accountService.queryAchievementStatisticApi(userId, oemCode, levelNo);
        return ResultVo.Success(statVO);
    }

    /**
     * @Description 推广中心-企业注册进度跟进
     * @Author yejian
     * @Date 2020/11/18 14:35
     * @Param userId
     * @Return ResultVo<CompanyRegProgressVO>
     */
    @ApiOperation("推广中心-企业注册进度跟进")
    @PostMapping("companyRegProgress")
    public ResultVo<CompanyRegProgressVO> companyRegProgress(@JsonParam Long userId) {
        if (null == userId) {
            return ResultVo.Fail("操作失败，用户ID不能为空！");
        }
        MemberExtendQuery query = new MemberExtendQuery();
        query.setUserId(userId);
        query.setOemCode(getRequestHeadParams("oemCode"));
        CompanyRegProgressVO regData = accountService.queryCompanyRegProgress(query);
        return ResultVo.Success(regData);
    }

    /**
     * @Description 推广中心-企业开票进度跟进
     * @Author yejian
     * @Date 2020/11/18 14:31
     * @Param userId
     * @Return ResultVo<CompanyRegProgressVO>
     */
    @ApiOperation("推广中心-企业开票进度跟进")
    @PostMapping("companyInvoiceProgress")
    public ResultVo<CompanyInvoiceProgressVO> companyInvoiceProgress(@JsonParam Long userId) {
        if (null == userId) {
            return ResultVo.Fail("操作失败，用户ID不能为空！");
        }
        MemberExtendQuery query = new MemberExtendQuery();
        query.setUserId(userId);
        query.setOemCode(getRequestHeadParams("oemCode"));
        CompanyInvoiceProgressVO regData = accountService.queryCompanyInvoiceProgress(query);
        return ResultVo.Success(regData);
    }

    /**
     * @Description 获取会员个人基本信息
     * @Author yejian
     * @Date 2020/11/12 14:30
     * @Return ResultVo
     */
    @ApiOperation("获取会员个人基本信息")
    @PostMapping("getBaseInfo")
    public ResultVo<MemberBaseInfoApiVO> getBaseInfo(@RequestBody @Valid MemberBaseInfoApiDTO entity, BindingResult results) {
        log.info("收到获取会员个人基本信息请求：{}", JSONObject.toJSONString(entity));
        if (results.hasErrors()) {
            return ResultVo.Fail(results);
        }
        entity.setOemCode(getRequestHeadParams("oemCode"));
        MemberBaseInfoApiVO baseInfo = accountService.getMemberBaseInfoApi(entity);
        return ResultVo.Success(baseInfo);
    }

    /**
     * 根据手机号查询账号
     */
    @ApiOperation("根据手机号查询账号")
    @PostMapping("queryAccount")
    public ResultVo<List<AccountVO>> queryAccount(@JsonParam String phone){
        String oemCode = this.getRequestHeadParams("oemCode");
        List<AccountVO>  list=new ArrayList<>();
        //查询后台账号
        AccountVO accountVOAdmin = this.userService.queryAccount(oemCode,phone);

        //查询小程序用户
        AccountVO accountVOApi = this.memberAccountService.queryMemberByPhone(phone,oemCode);

        if(accountVOAdmin!=null){
            //转换level等级
            if(accountVOAdmin.getBindAccountLevel()==4){
                accountVOAdmin.setBindAccountLevel(2);
            }else if(accountVOAdmin.getBindAccountLevel()==5){
                accountVOAdmin.setBindAccountLevel(3);
            }
            list.add(accountVOAdmin);
        }
        if(accountVOApi!=null){
            //转换level等级
            if(accountVOApi.getBindAccountLevel()==3){
                accountVOApi.setBindAccountLevel(0);
            }else if(accountVOApi.getBindAccountLevel()==5){
                accountVOApi.setBindAccountLevel(1);
            }
            list.add(accountVOApi);
        }

        return ResultVo.Success(list);
    }

    /**
     * 根据用户id查询收益情况
     */
    @PostMapping("queryEarnings")
    public ResultVo<MemberOrUserProfitsVO> queryEarnings(@RequestBody MemberProfitsGateWayQuery query){
        MemberOrUserProfitsVO vo=profitsDetailService.queryEarningsByUserId(query);
        return ResultVo.Success(vo);
    }
}