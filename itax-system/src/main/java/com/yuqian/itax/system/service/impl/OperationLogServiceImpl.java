package com.yuqian.itax.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.common.base.service.impl.BaseServiceImpl;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.error.entity.ErrorInfoEntity;
import com.yuqian.itax.system.dao.OperationLogMapper;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.entity.OperationLogEntity;
import com.yuqian.itax.system.service.OperationLogService;
import com.yuqian.itax.util.exception.ExceptionEnum;
import com.yuqian.itax.util.exception.NotLoginException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service("operationLogService")
public class OperationLogServiceImpl extends BaseServiceImpl<OperationLogEntity,OperationLogMapper> implements OperationLogService {

    @Autowired
    protected RedisService redisService;

    @Override
    public void addOperationLog(int oprType, String ipAddr, String className, String method, String result, String oemCode, String token) {
        OperationLogEntity operationLog = new OperationLogEntity();

        try {
            //获取登陆用户
            String currUserJson = redisService.get(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + token);
            if(StringUtils.isNotBlank(currUserJson)) {
                JSONObject currUserObject = JSONObject.parseObject(currUserJson);
                operationLog.setOprUserId(currUserObject.getLong("userId"));
                operationLog.setAddUser(currUserObject.getString("useraccount"));
            }
            operationLog.setOprTime(new Date());
            operationLog.setOprType(oprType);
            operationLog.setIpAddr(ipAddr);
            operationLog.setClassName(className);
            operationLog.setMethod(method);

            //解析返回结果
            boolean flag = isJSON2(result);
            if(flag){
                JSONObject resultObject = JSONObject.parseObject(result);
                if("0000".equals(resultObject.getString("retCode"))){
                    operationLog.setResult(1);//操作结果 0：失败 1：成功
                    operationLog.setRemark("请求成功");
                }else{
                    operationLog.setResult(0);//操作结果 0：失败 1：成功
                    operationLog.setRemark("请求失败");
                }
            }else{
                if("SUCCESS".equals(result) || "\"SUCCESS\"".equals(result)){
                    operationLog.setResult(1);//操作结果 0：失败 1：成功
                    operationLog.setRemark("请求成功");
                }else{
                    operationLog.setResult(0);//操作结果 0：失败 1：成功
                    operationLog.setRemark("请求失败");
                }
            }
            operationLog.setAddTime(new Date());
            mapper.insertSelective(operationLog);
        }catch (Exception e){
           log.error(e.getMessage());
        }
    }

    /**
     * 判断字符串是否是json格式
     */
    public static boolean isJSON2(String str) {
        boolean result = false;
        if (StringUtils.isNotBlank(str)) {
            str = str.trim();
            if (str.startsWith("{") && str.endsWith("}")) {
                result = true;
            } else if (str.startsWith("[") && str.endsWith("]")) {
                result = true;
            }
        }
        return result;
    }

    public static void main(String[] args){
        String str = "\"SUCCESS\"";
        System.out.println(isJSON2(str));
    }
}

