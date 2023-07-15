package com.yuqian.itax.admin.filter;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.util.exception.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 必传参数校验
 * 
 * @author LiuXianTing
 */
@Component
@WebFilter(urlPatterns = "/*")
@Slf4j
@Order(value = 2)
@SuppressWarnings("all")
public class ParamsRequiredFilter implements Filter {

	private RedisService redisService;

	private DictionaryService sysDictionaryService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext sc = filterConfig.getServletContext();
		WebApplicationContext cxt = (WebApplicationContext)WebApplicationContextUtils.getWebApplicationContext(sc);

		if(cxt != null && cxt.getBean("redisService") != null && redisService == null){
			redisService = (RedisService) cxt.getBean("redisService");
		}

		if(cxt != null && cxt.getBean("sysDictionaryService") != null && sysDictionaryService == null){
			sysDictionaryService = (DictionaryService) cxt.getBean("sysDictionaryService");
		}
		log.info("ParamsRequiredFilter init...");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json;charset=utf-8");
		String uri = httpServletRequest.getRequestURI();
		String oemCode = httpServletRequest.getHeader("oemCode");
		String token = httpServletRequest.getHeader("token");
		if((StringUtils.isNotBlank(oemCode) && StringUtils.isNotBlank(token)
				|| uri.indexOf("/doc.html") >-1 || uri.indexOf("/queryRegisteredName") >-1
				|| uri.indexOf("/queryOemName") >-1 || uri.indexOf("/verficationCode") >-1|| uri.indexOf("/changePasswordByphone") >-1
				|| uri.indexOf("/swagger-ui.html") >-1 || uri.indexOf("/swagger-resources") >-1
				|| uri.indexOf("/swagger") >-1 || uri.indexOf("/v2/api-docs") >-1
				|| uri.indexOf("/webjars") >-1 || uri.indexOf("/aliyun/oss/callback") >-1)) {
			boolean flag =checkOem(token,oemCode,(HttpServletResponse) response);
			if(!flag){
				return ;
			}
			chain.doFilter(request, response);
			return ;
		}/*else if(StringUtils.isBlank(oemCode)) {
			response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.PARAMS_EXCEPTION.getRetCode(), "未找到oemCode信息")));
			return;
		}*/else{
			boolean flag =checkOem(token,oemCode,(HttpServletResponse) response);
			if(!flag){
				return;
			}

			if(uri.indexOf("/login") > -1 || StringUtils.isNotBlank(token)){
				String currUserJson = redisService.get(RedisKey.LOGIN_TOKEN_KEY +oemCode+"_"+ token);
				if(uri.indexOf("/login") <0  && StringUtils.isBlank(currUserJson)) {
					response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ExceptionEnum.NO_LOGIN.getCode()+"", ExceptionEnum.NO_LOGIN.getMsg())));
					return;
				}
				chain.doFilter(request, response);
				return ;
			}else{
				response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.PARAMS_EXCEPTION.getRetCode(), "未找到token信息")));
				return;
			}
		}
	}

	@Override
	public void destroy() {
		log.info("WhiteListFilter destroy...");
	}

	private boolean checkOem(String token,String oemCode,HttpServletResponse response) throws IOException{
		if(StringUtils.isNotBlank(token)) {
			refreshTokenOutTime(token, oemCode);
		}
//		String oemJson = redisService.get(RedisKey.OEM_CODE_KEY + oemCode);
//		if(StringUtils.isBlank(oemJson)){
//			response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.SERVICE_EXPIRATION.getRetCode(), "已暂停服务，请联系客服确认！")));
//			return false;
//		}else{
//			OemEntity entity = JSON.parseObject(oemJson, OemEntity.class);
//			if(entity == null || entity.getOemStatus() != 1){
//				response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.SERVICE_EXPIRATION.getRetCode(), "已暂停服务，请联系客服确认！")));
//				return false;
//			}
//		}
		return true;
	}

	/**
	 * 刷新登陆token时间
	 * @param token
	 * @param oemCode
	 */
	private void refreshTokenOutTime(String token,String oemCode){
		String currUserJson = redisService.get(RedisKey.LOGIN_TOKEN_KEY +oemCode+"_"+ token);
		if(StringUtils.isNotBlank(currUserJson)){
			String outTime = redisService.get(RedisKey.LOGIN_TOKEN_KEY +"_yuncaiol_redisOutTime_sys");
			if(StringUtils.isBlank(outTime)) {
				outTime = sysDictionaryService.getByCode("redis_token_outtime_sys").getDictValue();
				redisService.set(RedisKey.LOGIN_TOKEN_KEY +"_yuncaiol_redisOutTime_sys",outTime,600); //设置10分钟的失效时间
			}
			CurrUser currUser = JSON.parseObject(currUserJson, CurrUser.class);
			redisService.set(RedisKey.LOGIN_TOKEN_KEY +oemCode+"_"+ token,currUser,Integer.parseInt(outTime)); //失效时间
			redisService.set(RedisKey.LOGIN_TOKEN_KEY +oemCode+"_" + "userId_2_" + currUser.getUseraccount(),token,Integer.parseInt(outTime));
		}
	}
}
