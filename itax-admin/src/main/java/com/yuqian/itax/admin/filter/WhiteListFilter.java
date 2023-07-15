package com.yuqian.itax.admin.filter;

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
		HttpServletResponse httpResponse = (HttpServletResponse) response;
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
        chain.doFilter(request, response); 
	}

	@Override
	public void destroy() {
		log.info("WhiteListFilter destroy...");
	}
}
