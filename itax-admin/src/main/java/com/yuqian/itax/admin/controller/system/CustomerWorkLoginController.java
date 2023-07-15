package com.yuqian.itax.admin.controller.system;

import com.yuqian.itax.admin.annotation.JsonParam;
import com.yuqian.itax.admin.controller.BaseController;
import com.yuqian.itax.common.base.vo.CustomerWorker;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.message.enums.MessageEnum;
import com.yuqian.itax.user.entity.CustomerServiceWorkNumberEntity;
import com.yuqian.itax.user.service.CustomerServiceWorkNumberService;
import com.yuqian.itax.util.util.MemberPsdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * @author：HZ
 * @Date：2019/12/6 11:12
 * @version：1.0
 */
@RestController
@RequestMapping("/customerServiceWork")
public class CustomerWorkLoginController extends BaseController {
    @Autowired
    CustomerServiceWorkNumberService customerServiceWorkNumberService;
    /**
     * 登录,由过滤器完成
     *
     * @author
     * @param useraccount  账号
     * @param password     密码
     * @return
     */
    @PostMapping("/login")
    public ResultVo login(@JsonParam String useraccount, @JsonParam String password) {
        if(StringUtils.isEmpty(StringUtils.isEmpty(useraccount) || StringUtils.isEmpty(password))){
            return ResultVo.Fail("账号密码不能为空");
        }
        //查询工号用户
        CustomerServiceWorkNumberEntity customerServiceWorkNumberEntity=new CustomerServiceWorkNumberEntity();
        customerServiceWorkNumberEntity.setWorkNumber(useraccount);
        customerServiceWorkNumberEntity.setUserId(getCurrUserId());
        customerServiceWorkNumberEntity.setStatus(1);
        CustomerServiceWorkNumberEntity entity=customerServiceWorkNumberEntity=customerServiceWorkNumberService.selectOne(customerServiceWorkNumberEntity);
        if(null == entity){
            return ResultVo.Fail("工号账号不存在！");
        }
        if(!entity.getUserId().equals(getCurrUserId())){
            return ResultVo.Fail("工号账号不是"+getCurrUseraccount()+"名下账号。");
        }
        String 	pwd= MemberPsdUtil.encrypt(password, entity.getWorkNumber(),entity.getSlat());

        if (!entity.getWorkNumberPwd().equals(pwd) ) {
            return ResultVo.Fail("账号或密码错误");
        }else if(entity.getStatus() == 2){
            return ResultVo.Fail("账号已被锁定，请联系管理员");
        }else if(entity.getStatus() == 0){
            return ResultVo.Fail("账号已被禁用，请联系管理员");
        }
        String token = createToken();
        String oemCode=getRequestHeadParams("oemCode");
        CustomerWorker customerWorker = new CustomerWorker(entity.getId(),entity.getWorkNumber(),oemCode,null);
        redisService.set(RedisKey.WORKER_LOGIN_TOKEN_KEY+"_"+oemCode+"_"+ token,customerWorker,60*60*10);


        Map<String,String> result = new HashMap<>();
        result.put("workToken",token);
        return  ResultVo.Success(result);
    }


    /**
     * 工号修改密码
     */
    @PostMapping("/resetPassword")
    public ResultVo resetPassword(@JsonParam String oldPassword, @JsonParam String newPassword,@JsonParam String affirmPassword) {
        if(StringUtils.isEmpty(StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(affirmPassword))){
            return ResultVo.Fail("请输入密码！");
        }
        if(!newPassword.equals(affirmPassword)){
            return ResultVo.Fail("确认密码和新密码不一致！");
        }
        //查询工号用户
        CustomerServiceWorkNumberEntity entity=customerServiceWorkNumberService.findById(getCustomerWorker().getUserId());
        if(null == entity){
            return ResultVo.Fail("工号账号不存在！");
        }
        String 	pwd= MemberPsdUtil.encrypt(oldPassword, entity.getWorkNumber(),entity.getSlat());

        if (!entity.getWorkNumberPwd().equals(pwd) ) {
            return ResultVo.Fail("账号或密码错误");
        }else if(entity.getStatus() == 2){
            return ResultVo.Fail("账号已被锁定，请联系管理员");
        }else if(entity.getStatus() == 0){
            return ResultVo.Fail("账号已被禁用，请联系管理员");
        }
        String 	newPwd= MemberPsdUtil.encrypt(newPassword, entity.getWorkNumber(),entity.getSlat());

        entity.setWorkNumberPwd(newPwd);
        customerServiceWorkNumberService.editByIdSelective(entity);

        return  ResultVo.Success();
    }

    @PostMapping("/exit")
    public ResultVo exit(){
            String token=getRequestHeadParams("workToken");
        if(null == token){
            return ResultVo.Fail("您没有登陆！");
        }
        try {
            redisService.delete(RedisKey.WORKER_LOGIN_TOKEN_KEY+"_"+ token);
        } catch (Exception e) {
            return ResultVo.Fail(MessageEnum.SYSTEM_ERROR.getMessage());
        }
        return ResultVo.Success("退出成功");
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
}
