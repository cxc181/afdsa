package com.yuqian.itax.gateway.filter;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
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
public class ParamsRequiredFilter implements Filter {

	private RedisService redisService;

	private OemService oemService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext sc = filterConfig.getServletContext();
		WebApplicationContext cxt = (WebApplicationContext)WebApplicationContextUtils.getWebApplicationContext(sc);

		if(cxt != null && cxt.getBean("redisService") != null && redisService == null){
			redisService = (RedisService) cxt.getBean("redisService");
		}
		if(cxt != null && cxt.getBean("oemService") != null && oemService == null){
			oemService = (OemService) cxt.getBean("oemService");
		}
		log.info("ParamsRequiredFilter init...");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		log.info("WhiteListFilter destroy...");
	}

	@SuppressWarnings(value="all")
	private boolean checkOem(String oemCode,HttpServletResponse response) throws IOException{
		String oemJson = redisService.get(RedisKey.OEM_CODE_KEY + oemCode);
		if(StringUtils.isBlank(oemJson)){
			OemEntity entity = oemService.getOem(oemCode);
			if(entity == null) {
				response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.SERVICE_EXPIRATION.getRetCode(), "已暂停服务，请联系客服确认！")));
				return false;
			}else{
				redisService.set(RedisKey.OEM_CODE_KEY + entity.getOemCode(),entity);
				if(entity.getOemStatus() != 1){
					response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.SERVICE_EXPIRATION.getRetCode(), "已暂停服务，请联系客服确认！")));
					return false;
				}
			}
		}else{
			OemEntity entity = JSON.parseObject(oemJson, OemEntity.class);
			if(entity == null || entity.getOemStatus() != 1){
				response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.SERVICE_EXPIRATION.getRetCode(), "已暂停服务，请联系客服确认！")));
				return false;
			}
		}
		return true;
	}
}
