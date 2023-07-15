package com.yuqian.itax.api.filter;

import com.yuqian.itax.util.util.XssAndSqlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName MposSystemBootstrap
 * @Description 防止 XSS、 SQL注入攻击
 * @Author jiangni
 * @Date 2019/7/15
 * @Version 1.0
 */
@Component
public class XssAndSqlFilter extends OncePerRequestFilter {

	private static final String METHOD_GET = "GET";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		XssHttpServletRequestWrapper xssHttpServletRequestWrapper = new XssHttpServletRequestWrapper(request);
		if (!METHOD_GET.equals(request.getMethod())) {
			filterChain.doFilter(xssHttpServletRequestWrapper, response);
		}else{
		    super.doFilter(request,response,filterChain);
        }
	}

	public static class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

		/**
		 * Constructs a request object wrapping the given request.
		 *
		 * @param request
		 *            The request to wrap
		 * @throws IllegalArgumentException
		 *             if the request is null
		 */
		public XssHttpServletRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String getParameter(String name) {
			String value = super.getParameter(name);
			return XssAndSqlUtils.xssEncode(value);
		}

		@Override
		public String getHeader(String name) {
			String value = super.getHeader(name);
			return XssAndSqlUtils.xssEncode(value);
		}

		@Override
		@SuppressWarnings("all")
		public String[] getParameterValues(String name) {
			String[] values = super.getParameterValues(name);
			if (values == null) {
				return null;
			}
			int count = values.length;
			String[] encodedValues = new String[count];
			for (int i = 0; i < count; i++) {
				encodedValues[i] = XssAndSqlUtils.xssEncode(values[i]);
			}
			return encodedValues;
		}

	}
}
