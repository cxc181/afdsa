package com.yuqian.itax.api.controller;

import com.alibaba.fastjson.JSON;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.system.entity.DictionaryEntity;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.user.service.MemberAccountService;
import com.yuqian.itax.util.exception.ExceptionEnum;
import com.yuqian.itax.util.exception.NotLoginException;
import com.yuqian.itax.util.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @ClassName BaseController
 * @Description 基础信息controller
 * @Author Kaven
 * @Date 2019/12/06
 * @Version 1.0
 */
public class BaseController {

    @Autowired
    protected RedisService redisService;

    @Autowired
    protected DictionaryService sysDictionaryService;

    @Autowired
    protected MemberAccountService memberAccountService;

    /**
     * 获取httpservletrequest请求
     *
     * @return
     */
    protected HttpServletRequest getRequest() {
        RequestAttributes reqAttrs = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)reqAttrs;
        return servletRequestAttributes.getRequest();
    }

    /**
     * 从httpservlet中根据请求参数名称获取参数值
     *
     * @param paramName
     * @return
     */
    protected String getRequestParams(String paramName) {
        return getRequest().getParameter(paramName);
    }

    /**
     * 从httpservlet中根据请求参数名称获取参数值
     *
     * @param paramName
     * @return
     */
    protected String getRequestHeadParams(String paramName) {
        String params = getRequest().getHeader(paramName);
        if (StringUtil.isBlank(params) || Objects.equals("null", params)) {
            return "";
        }
        return params;
    }
    /**
     * 获取当前用户信息
     *
     * @author LiuXianTing
     * @return
     */
    protected CurrUser getCurrUser() {
        String token = getRequestHeadParams("token");
        String oemCode = getRequestHeadParams("oemCode");
        String currUserJson = redisService.get(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + token);
        if(StringUtils.isBlank(currUserJson)) {
            throw new NotLoginException(ExceptionEnum.NO_LOGIN);
        }
        //重新设置redis的过期时间
        DictionaryEntity entity = sysDictionaryService.getByCode("redis_token_outtime");
        redisService.set(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_"+ token,currUserJson,Integer.parseInt(entity.getDictValue()));
        return JSON.parseObject(currUserJson, CurrUser.class);
    }

    /**
     * 获取当前用户id
     *
     * @author jiangni
     * @param
     * @return
     */
    protected Long  getCurrUserId() {
        return getCurrUser().getUserId();
    }

    /**
     * 获取当前用户账号
     *
     * @author jiangni
     * @param
     * @return
     */
    protected String getCurrUseraccount() {
        return getCurrUser().getUseraccount();
    }

    /**
     * 获取当前账户团队id
     * @return
     */
    protected Integer getCurrUserGroupId() {
        return getCurrUser().getGroupId();
    }
    /**
     * 允许未登录的调用
     * @param notLogin
     * @return
     */
    protected CurrUser getCurrUserGroupIdNotLogin(boolean notLogin) {
        if(notLogin) {
            try {
                return getCurrUser();
            } catch (NotLoginException e) {
                return null;
            }
        }
        return getCurrUser();
    }
}
