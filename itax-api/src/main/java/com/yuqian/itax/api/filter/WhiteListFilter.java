package com.yuqian.itax.api.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 白名单过滤器
 * 解决跨域问题
 * 
 * @author LiuXianTing
 */
@Component
@WebFilter(urlPatterns = "/*")
@Slf4j
@Order(value = 1)
public class WhiteListFilter implements Filter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("WhiteListFilter init...");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
		httpServletResponse.setContentType("text/html;charset=utf-8");
		httpServletResponse.setHeader("Access-Control-Allow-Headers",
				"Origin,Accept,x-requested-with,Content-Type,X-AUTH-SESSION,Content-Security-Policy,X-Content-Type-Options,X-XSS-Protection,token,workToken,oemCode,sourceType");
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		log.info("WhiteListFilter destroy...");
	}
}
