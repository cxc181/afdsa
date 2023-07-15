package com.yuqian.itax.gateway.aspect;

import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.util.util.DateUtil;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @ClassName OperatorLogAspect
 * @Description  全部方法日志记录
 * @Author jiangni
 * @Date 2019/7/15
 * @Version 1.0
 */
@Aspect
@Component
@Slf4j
public class OperatorLogAspect {
	//@Pointcut("execution(public * com.yuqian.itax..*.*(..)) " +
	//		"&& !execution(public * com.yuqian.itax.*.aspect..*.*(..)) " +
	//		"&& !execution(public * com.yuqian.itax.*.config..*.*(..)) " +
	//		"&& !execution(public * com.yuqian.itax.*.filter..*.*(..))")//扫描所有包
    @Pointcut("execution(public * com.yuqian.itax.gateway.controller..*(..))")
	public void pointCutMethod() {

	}

	/**
	 * 环绕获取数据
	 *
	 * @param joinPoint
	 */
	@Around("pointCutMethod()") // 使用上面定义的切入点
	public Object handle(ProceedingJoinPoint joinPoint) throws Throwable{
        Long startTime = DateUtil.getCurrentTimes();

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

		log.info("【请求 oemCode】：{}，【请求 token】：{}", request.getHeader("oemCode"), request.getHeader("token"));
		log.info("【请求 URL】：{}", request.getRequestURL());
		log.info("【请求 IP】：{}", request.getRemoteAddr());
		log.info("【请求类名】：{}，【请求方法名】：{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

		// 循环打印请求参数
		Object[] args = joinPoint.getArgs();
		args = streamOf(args)
				.filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof BindingResult) && !(arg instanceof HttpServletResponse)))
				.toArray();
		ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		String[] parameterNames = pnd.getParameterNames(method);
		log.info("【请求参数】：");
		int argsLen = args.length;
		for (int i = 0; i < parameterNames.length; i++) {
			if (argsLen > 0 && argsLen > i) {
				log.info("【key】：{}，【value】：{}", parameterNames[i], JSONObject.toJSONString(args[i]));
			} else {
				log.info("【key】：{}，【value】：{}", parameterNames[i], null);
			}
		}

		Object result = joinPoint.proceed();
		String resultMessage = "";
		try {
			resultMessage = JSONObject.toJSONString(result);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		log.info("【返回值】：{}", resultMessage);

        Long endTime = DateUtil.getCurrentTimes();
        log.info("【请求耗时】：{}毫秒", endTime - startTime);

		String header = request.getHeader("User-Agent");
		UserAgent userAgent = UserAgent.parseUserAgentString(header);

		log.info("【浏览器类型】：{}，【操作系统】：{}，【原始User-Agent】：{}", userAgent.getBrowser().toString(), userAgent.getOperatingSystem().toString(), header);
		return result;
	}

	@AfterThrowing(value = "pointCutMethod()",throwing = "ex") // 使用上面定义的切入点进行异常通知
	public void handleAfterThrowing(JoinPoint joinPoint, Exception ex) throws Throwable{
		Long startTime = DateUtil.getCurrentTimes();

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

		String resultMessage = ex.getMessage();
		log.info("【返回值】：{}", resultMessage);

		Long endTime = DateUtil.getCurrentTimes();
		log.info("【请求耗时】：{}毫秒", endTime - startTime);

		String header = request.getHeader("User-Agent");
		UserAgent userAgent = UserAgent.parseUserAgentString(header);
		log.info("【浏览器类型】：{}，【操作系统】：{}，【原始User-Agent】：{}", userAgent.getBrowser().toString(), userAgent.getOperatingSystem().toString(), header);
	}

	/**
	 * @Author Kaven
	 * @Description 数组转对象
	 * @Date 9:47 2019/9/7
	 * @Param [array]
	 * @return java.util.stream.Stream<T>
	 **/
	public static <T> Stream<T> streamOf(T[] array) {
		return ArrayUtils.isEmpty(array) ? Stream.empty() : Arrays.asList(array).stream();
	}
}
