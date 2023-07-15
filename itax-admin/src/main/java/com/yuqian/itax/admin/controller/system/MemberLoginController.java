package com.yuqian.itax.admin.controller.system;

import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.message.enums.VerifyCodeTypeEnum;
import com.yuqian.itax.message.service.SmsService;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.entity.UserExtendEntity;
import com.yuqian.itax.user.entity.query.MemberLoginQuery;
import com.yuqian.itax.user.entity.vo.UserAndExtendVO;
import com.yuqian.itax.user.service.UserExtendService;
import com.yuqian.itax.util.util.ActivationCode;
import com.yuqian.itax.util.util.DateUtil;
import com.yuqian.itax.util.util.MemberPsdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author：HZ
 * @Date：2019/12/6 11:12
 * @version：1.0
 */
@RestController
@RequestMapping("/memberLogin")
@Slf4j
public class MemberLoginController extends BaseController {
    @Autowired
    private SmsService smsService;
    @Autowired
    private UserExtendService userExtendService;
    @Autowired
    OemParamsService oemParamsService;
    @Autowired
    OemService oemService;
    /**
     * 登录,由过滤器完成
     *
     * @author
     * @param memberLoginQuery
     * @return
     */
    @PostMapping("/login")
    public ResultVo login(@RequestBody MemberLoginQuery memberLoginQuery) {
        if(StringUtils.isEmpty(memberLoginQuery.getUseraccount()) || StringUtils.isEmpty(memberLoginQuery.getPassword())){
            return ResultVo.Fail("账号密码不能为空");
        }
        if( StringUtils.isEmpty(memberLoginQuery.getVCode())){
            return ResultVo.Fail("验证码不能为空");
        }
        String oemCode=getRequestHeadParams("oemCode");
        Map<String,String> map=new HashMap<>();
        memberLoginQuery.setOemCode(oemCode);
        try {
            //if(!subject.isAuthenticated()) {
                //UsernamePasswordToken uptoken = new UsernamePasswordToken(memberLoginQuery.getUseraccount(), memberLoginQuery.getPassword());
                //uptoken.setRememberMe(true);
                //subject.login(uptoken);
            //}
            map =userService.login(memberLoginQuery.getOemCode(),memberLoginQuery.getUseraccount(),memberLoginQuery.getPassword(),memberLoginQuery.getVCode());
        }catch (BusinessException e){
            return ResultVo.Fail(e.getMessage());
        }

        //调用登录日志接口
        //loginLogService.insertLoginLog(memberLoginQuery.getUseraccount(),getRequest().getRemoteAddr(),getRequest().getHeader("appCode"),"登录操作!");
        return ResultVo.Success(map);
    }


    /**
     * 生成登录token 先生成uuid+5位随机数 散列后 在md5加密
     *
     * @author LiuXianTing
     * @return
     */
    //@OperatorLog(module="登录",operDes="生成登录token 先生成uuid+5位随机数 散列后 在md5加密",oprType=4)
    private String createToken() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }


    @PostMapping("/exit")
    public ResultVo exit(){
        String token=getRequestHeadParams("token");
        String oemCode=getRequestHeadParams("oemCode");
        if(null == token){
            return ResultVo.Fail("您没有登陆！");
        }
        try {
            redisService.delete(RedisKey.LOGIN_TOKEN_KEY +oemCode+"_"+ token);
        } catch (Exception e) {
            return ResultVo.Fail(MessageEnum.SYSTEM_ERROR.getMessage());
        }
        return ResultVo.Success("退出成功");
    }

    /**
     * 修改密码
     */
    @PostMapping("/resetPassword")
    public ResultVo resetPassword(@JsonParam String oldPassword, @JsonParam String newPassword, @JsonParam String affirmPassword,@JsonParam String verficationCode) {
        if(StringUtils.isEmpty(StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(affirmPassword))){
            return ResultVo.Fail("请输入密码！");
        }
        if(!newPassword.equals(affirmPassword)){
            return ResultVo.Fail("确认密码和新密码不一致！");
        }
        if(StringUtils.isEmpty(verficationCode)){
            return ResultVo.Fail("请输入验证码！");
        }
        //查询工号用户
        UserEntity entity=userService.findById(getCurrUserId());
        if(null == entity){
            return ResultVo.Fail("账号不存在！");
        }
        String oemCode = entity.getOemCode();
        if(StringUtils.isEmpty(entity.getOemCode())){
            oemCode = "";
        }
        //验证码对比
        String verfication=redisService.get(RedisKey.SMS_UPDATE_PWD_KEY_SUFFER+oemCode+"_"+entity.getUsername());
        if(StringUtils.isEmpty(verfication)||!verficationCode.equals(verfication)){
            return ResultVo.Fail(MessageEnum.PASSWORD_RESET_CODE_IS_EXPIRED.getMessage());
        }
        String 	pwd= MemberPsdUtil.encrypt(oldPassword, entity.getUsername(),entity.getSlat());

        if (!entity.getPassword().equals(pwd) ) {
            return ResultVo.Fail("账号或密码错误");
        }else if(entity.getStatus() == 2){
            return ResultVo.Fail("账号已被锁定，请联系管理员");
        }else if(entity.getStatus() == 0){
            return ResultVo.Fail("账号已被禁用，请联系管理员");
        }
        String 	newPwd= MemberPsdUtil.encrypt(newPassword, entity.getUsername(),entity.getSlat());

        entity.setPassword(newPwd);
        userService.editByIdSelective(entity);

        return  ResultVo.Success();
    }

    /**
     * 更具手机号修改密码(忘记密码)
     */
    @PostMapping("/changePasswordByphone")
    public ResultVo changePasswordByphone(@JsonParam String userName, @JsonParam String verficationCode,@JsonParam String nPassWord ,@JsonParam String newCPassWord,@JsonParam String oemCode){
        //参数校验
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(verficationCode)|| StringUtils.isEmpty(nPassWord)|| StringUtils.isEmpty(newCPassWord) ) {
            return ResultVo.Fail(MessageEnum.REQUEST_PARAMETER_ERROR.getMessage());
        }
        if (StringUtils.isEmpty(userName)) {
            return ResultVo.Fail("用户名不能为空");
        }
        if(!nPassWord.equals(newCPassWord)){
            return ResultVo.Fail("新密码和确认密码不一致，请确认后再提交");
        }
        //根据账号查询用户
        try {
            UserEntity userEntity = userService.getUserByUserName(userName, oemCode);
            if (userEntity == null) {
                return ResultVo.Fail(MessageEnum.PASSWORD_RESET_MEMBER_NO_IS_ERROR.getMessage());
            }
            //查询用户扩展信息
            UserExtendEntity userExtendEntity = userExtendService.getUserExtendByUserId(userEntity.getId());
            if (userExtendEntity == null) {
                return ResultVo.Fail("用户扩展信息不存在");
            }
            //验证码对比
            String verfication=redisService.get(RedisKey.SMS_RESET_PWD_KEY_SUFFER+oemCode+"_"+userEntity.getUsername());
            if(StringUtils.isEmpty(verfication)||!verficationCode.equals(verfication)){
                return ResultVo.Fail(MessageEnum.PASSWORD_RESET_CODE_IS_EXPIRED.getMessage());
            }
            //修改会员密码
            String newPassWordMD5MemberPsdUtil=MemberPsdUtil.encrypt(nPassWord, userEntity.getUsername(),userEntity.getSlat());
            userEntity.setPassword(newPassWordMD5MemberPsdUtil);
            userService.editByIdSelective(userEntity);

            //删除登陆限制
            String userOemCode = userEntity.getOemCode();
            if(StringUtils.isEmpty(userOemCode)){
                userOemCode = "";
            }
            redisService.delete(RedisKey.USER_LOGIN_FAIL_KEY+userOemCode+"_"+ userEntity.getUsername());
            redisService.delete(RedisKey.USER_LOGIN_LOCK_KEY+userOemCode+"_"+ userEntity.getUsername());
        } catch (BusinessException e) {
            return ResultVo.Fail(e.getMessage());
        } catch (Exception e) {
            return ResultVo.Fail("密码修改失败");
        }
        return  ResultVo.Success("密码修改成功!");
    }


    /**
     * 获取验证码
     */
    @PostMapping("/verficationCode")
    public  ResultVo getVerficationCode(@JsonParam String type,@JsonParam String oemCode,@JsonParam String username,@JsonParam String password){
        //获取验证码
        if(StringUtils.isEmpty(username)){
            return  ResultVo.Fail("请输入用户账号");
        }
        UserAndExtendVO userAndExtendVO=userService.qeruyUserByUsernameAndOemCode(oemCode,username,null);
        if(null == userAndExtendVO){
            return  ResultVo.Fail("账号不存在");
        }
        try {
            if(!"CHANGEPASSWORD".equals(type)) {
                boolean flag = userService.loginSms(oemCode, username, password);
            }
        }catch (Exception e){
            return ResultVo.Fail(e.getMessage());
        }
        OemParamsEntity params = oemParamsService.getParams(oemCode, OemParamsTypeEnum.SMS_NUMBER.getValue());
        if (null == params) {
            return ResultVo.Fail("未配置近两小时允许发送短信数！");
        }
        OemEntity oemEntity=oemService.getOem(oemCode);
        if(oemEntity==null){
            return  ResultVo.Fail("机构不存在");
        }
        String  verficationCode;
        if(StringUtils.isEmpty(oemEntity.getDefaultSmsCode())){
            verficationCode= ActivationCode.getActivationCode();
        }else{
            verficationCode=oemEntity.getDefaultSmsCode();
        }
        //设置发送次数
        int change=  redisService.get(RedisKey.USER_SMS_COUNT_KEY+"_"+oemCode+"_"+userAndExtendVO.getUsername())==null?0:Integer.parseInt(redisService.get(RedisKey.USER_SMS_COUNT_KEY+"_"+oemCode+"_"+userAndExtendVO.getUsername()));
        change++;
        if(change>Integer.parseInt(params.getParamsValues())){
            return  ResultVo.Fail("今日验证码发送次数已到上限！");
        }
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long outTime;
        try {
            outTime=DateUtil.diffDateTime(sdf2.parse(sdf.format(new Date())+" 23:59:59"),new Date());
        } catch (ParseException e) {
            return ResultVo.Fail("日期格式错误"+e.getMessage());
        }
        redisService.set(RedisKey.USER_SMS_COUNT_KEY+"_"+oemCode+"_"+userAndExtendVO.getUsername(),change,outTime.intValue());//timeout秒为单位
        String  smsTemplateType="";
        if("CHANGEPASSWORD".equals(type)){
            //设置验证码
            smsTemplateType= VerifyCodeTypeEnum.CHANGE_PASSWORD.getValue();
            redisService.set(RedisKey.SMS_RESET_PWD_KEY_SUFFER+oemCode+"_"+userAndExtendVO.getUsername(),verficationCode,300);//timeout秒为单位
        }else if("LOGIN".equals(type)){
            smsTemplateType=VerifyCodeTypeEnum.LOGIN.getValue();
            //设置验证码
            redisService.set(RedisKey.SMS_USER_LOGIN_KEY_SUFFER+oemCode+"_"+userAndExtendVO.getUsername(),verficationCode,300);//timeout秒为单位
        }else if("UPDATEPASSWORD".equals(type)){
            smsTemplateType=VerifyCodeTypeEnum.CHANGE_PASSWORD.getValue();
            //设置验证码
            redisService.set(RedisKey.SMS_UPDATE_PWD_KEY_SUFFER+oemCode+"_"+userAndExtendVO.getUsername(),verficationCode,300);//timeout秒为单位
        }else{
            return  ResultVo.Fail("验证码类型未知发送失败");
        }
            Map<String,Object> map=new HashMap();
        map.put("verficationCode",verficationCode);
        map.put("phone",org.apache.commons.lang3.StringUtils.overlay(userAndExtendVO.getPhone(), "****", 3, userAndExtendVO.getPhone().length() - 4));
        //发送短信
        Map<String,Object>  resultMap=null;
        //测试环境暂时屏蔽发送短信
        try {
            resultMap=smsService.sendTemplateSms(userAndExtendVO.getPhone(),oemCode, smsTemplateType, map,2);
            if (!"SUCCESS".equals(resultMap.get("code"))){
                return ResultVo.Fail((String)resultMap.get("message"));
            }
        } catch (BusinessException e) {
            log.error("获取验证码异常：{}",e.getMessage());
            return ResultVo.Fail(e.getMessage());
        }
        return ResultVo.Success(map);
    }
}
