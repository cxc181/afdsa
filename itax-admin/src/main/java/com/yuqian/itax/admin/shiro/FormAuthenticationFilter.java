package com.yuqian.itax.admin.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 表单验证（包含验证码）过滤类
 *
 * @date: 2017年10月13日 下午3:41:59 
 * @author 刘献廷
 */
@Component
@Slf4j
public class FormAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter {
	
	/**
	 * 用户登录帐号为空错误提示
	 */
	private static final String NULL_ACCOUNT_MSG = "用户登录帐号不能为空";
	/**
	 * 用户登录登录密码为空错误提示
	 */
	private static final String NULL_PASSWORD_MSG = "用户登录登录密码不能为空";
	/**
	 * 验证码为空错误提示
	 */
	private static final String NULL_VALIDATION_CODE = "验证码不存在或者错误";
	/**
	 * 登录失败
	 */
	private static final String LOGIN_FAIL = "帐号不存在或密码错误";
	/**
	 * 登录异常
	 */
	private static final String SYS_ERROR = "登录失败,系统错误！";
	
	/**
	 * 区分账号异常或验证码为空异常信息
	 */
	private int type;
	
	@Override
	protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) {
		UsernamePasswordToken token = (UsernamePasswordToken)super.createToken(servletRequest, servletResponse);
		String useraccount = servletRequest.getParameter("useraccount");
		token.setUsername(useraccount);
		String password = servletRequest.getParameter("password");
		if(StringUtils.isNotBlank(password)) {
			token.setPassword(password.toCharArray());
		}
		token.setRememberMe(StringUtils.equalsIgnoreCase(servletRequest.getParameter("rememberMe"), "on")); //记住我
		return token;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		//判断是否授权
		Subject subject=SecurityUtils.getSubject();
		if(!subject.isAuthenticated()){
			HttpServletRequest httpRequest = WebUtils.toHttp(request);
			HttpServletResponse httpResponse = WebUtils.toHttp(response);
			if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
//				if ("*".equals(originHeader)) {
					httpResponse.setContentType("application/json; charset=UTF-8");
					httpResponse.setCharacterEncoding("utf-8");
					httpResponse.setHeader("Access-control-Allow-Origin", "*");
					httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
					httpResponse.setHeader("Access-Control-Allow-Headers",
							"Origin,Accept,x-requested-with,Content-Type,X-AUTH-SESSION,Content-Security-Policy,X-Content-Type-Options,X-XSS-Protection,token,workToken,oemCode,sourceType");
					httpResponse.setHeader("Access-Control-Expose-Headers",
							"X-AUTH-SESSION,Content-Security-Policy,X-Content-Type-Options,X-XSS-Protection");
					httpResponse.setHeader("Access-Control-Max-Age", "3600");
					httpResponse.setHeader("Content-Security-Policy",
							"script-src 'self'; object-src 'none'; style-src cdn.example.org third-party.org; child-src https:");
					httpResponse.setHeader("X-Content-Type-Options", "nosniff");
					httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
					return false;
			}
		}
		return true;
	}

	@Override  
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {  
        HttpServletRequest req = (HttpServletRequest)request;  
        if(isLoginRequest(req, response) && StringUtils.equalsIgnoreCase("get", req.getMethod())) {//登陆请求
        	return true;
        }
        Subject subject = SecurityUtils.getSubject();
		if(subject.isAuthenticated()) {  
            return true;//已经登录过  
        }  
        return super.onPreHandle(request, response, mappedValue);
    }  
	
	@Override
	protected boolean executeLogin(ServletRequest servletRequest, ServletResponse servletResponse)
			throws Exception {
		String useraccount = servletRequest.getParameter("useraccount");
		if(StringUtils.isBlank(useraccount)) { //账号为空
			return onLoginFailure(createToken(servletRequest, servletResponse), new UnknownAccountException(NULL_ACCOUNT_MSG), servletRequest, servletResponse);
		}
		String password = servletRequest.getParameter("password");
		if(StringUtils.isBlank(password)) { //密码为空,密码规则不合法
			return onLoginFailure(createToken(servletRequest, servletResponse), new UnknownAccountException(NULL_PASSWORD_MSG), servletRequest, servletResponse);
		}
		type = 0;
		String validationCode = servletRequest.getParameter("validationCode");
		HttpServletRequest request=(HttpServletRequest)servletRequest;
		String attribute = (String) request.getSession().getAttribute("validation_code");
		if (!StringUtils.equalsIgnoreCase(validationCode, attribute)) {
			type = 1;
			return onLoginFailure(createToken(servletRequest, servletResponse), new UnknownAccountException(NULL_VALIDATION_CODE), servletRequest, servletResponse);
		}
		return super.executeLogin(servletRequest, servletResponse);
	}
	
	
	/**
	 * 登录成功 执行操作
	 * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#onLoginSuccess(AuthenticationToken, Subject, ServletRequest, ServletResponse)
	 */
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
			ServletRequest request, ServletResponse response)throws Exception {
		return super.onLoginSuccess(token, subject, request, response);
	}

	/**
	 * 登录失败执行操作
	 * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#onLoginFailure(AuthenticationToken, AuthenticationException, ServletRequest, ServletResponse)
	 */
	@Override
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,ServletResponse response) {
		String faliMsg;
		if(e instanceof UnknownAccountException || e instanceof IncorrectCredentialsException || e instanceof AuthenticationException) {//账号或者密码错误
			if (type==0) {
				faliMsg = LOGIN_FAIL;
			}else {
				faliMsg = NULL_VALIDATION_CODE;
			}
		}else if (e instanceof DisabledAccountException) {//账号或者密码错误
			faliMsg = e.getMessage();
		} else {
			faliMsg = SYS_ERROR;//其他异常
//			e.printStackTrace();
			log.error(e.getMessage());
		}
		request.setAttribute("errorMsg", faliMsg);
		return true;
	}
}