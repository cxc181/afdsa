package com.yuqian.itax.api.filter;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.vo.CurrUser;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.util.exception.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
public class ParamsRequiredFilter implements Filter {

	private RedisService redisService;

	private OemService oemService;

	private DictionaryService sysDictionaryService;

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
		if(uri.indexOf("/registerorder/wechatNotify.data") >-1 || uri.indexOf("/doc.html") >-1
				|| uri.indexOf("/wechatNotify") >-1 || uri.indexOf("/wechatNotifyByHand") >-1
				|| uri.indexOf("/bytedancePayNotify") >-1
				|| uri.indexOf("/getOemByCode") >-1 || uri.indexOf("/reStartProfitsDetail") >-1
				|| uri.indexOf("/swagger-ui.html") >-1 || uri.indexOf("/swagger-resources") >-1
				|| uri.indexOf("/swagger") >-1 || uri.indexOf("/v2/api-docs") >-1
				|| uri.indexOf("/webjars") >-1 || uri.indexOf("/v2/api-docs-ext") >-1
				|| uri.indexOf("/detailH5") >-1 || uri.indexOf("/point/add") >-1 || uri.indexOf("agreementTemplate/getAgreementTemplateInfo") >-1
				|| uri.indexOf("/allOemInfos") >-1 || uri.indexOf("/aliyun/oss/callback") >-1
				|| uri.indexOf("/yishui/notify") >-1){
			chain.doFilter(request, response);
			return ;
		}else if(StringUtils.isNotBlank(oemCode) && StringUtils.isNotBlank(token)) {
			boolean flag =checkOem(token,oemCode,(HttpServletResponse) response);
			if(!flag){
				return;
			}
			String currUserJson = redisService.get(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + token);
			if(StringUtils.isBlank(currUserJson)) {
				response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ExceptionEnum.NO_LOGIN.getCode(), ExceptionEnum.NO_LOGIN.getMsg())));
				return ;
			}
			chain.doFilter(request, response);
//			//同一个用户同一接口防止重复操作
//			String lockTime = System.currentTimeMillis() + 1000 + "";
//			boolean lockResult = redisService.lock(token+"_"+uri, lockTime, 1);
//			if(!lockResult){
//				response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.FAIL.getRetCode(), "请勿重复操作！")));
//				return ;
//			}
//			try {
//				chain.doFilter(request, response);
//			}catch (Exception e){
//				throw new BusinessException(e.getMessage());
//			}finally {
//				//释放redis锁
//				redisService.unlock(token+"_"+uri, lockTime);
//			}
			return ;
		}else if(StringUtils.isBlank(oemCode) && uri.indexOf("/allOemInfos")<0) {
			response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.PARAMS_EXCEPTION.getRetCode(), "未找到oemCode信息")));
			return;
		}else {
			boolean flag =checkOem(token,oemCode,(HttpServletResponse) response);
			if(!flag){
				return;
			}

			if(uri.indexOf("/login") > -1 || uri.indexOf("/sms/verifyCode") >-1 || uri.indexOf("/dict/getDictByCode") >-1
					|| uri.indexOf("/common/getProblems") >-1 || uri.indexOf("/product/listProduct") >-1 || uri.indexOf("/common/getBanners") >-1
					|| uri.indexOf("/user/getMemberName") >-1 || uri.indexOf("/common/banner/detail") >-1 || uri.indexOf("/common/getProblem") >-1
					|| uri.indexOf("/vatincometax/calculateIncomeTax")>-1 || uri.indexOf("/vatincometax/queryTaxRules")>-1
					|| uri.indexOf("/vatincometax/calculateVatTax")>-1 || uri.indexOf("/system/info/findParkList")>-1){
				chain.doFilter(request, response);
				return ;
			}else{
				response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ExceptionEnum.NO_LOGIN.getCode(), ExceptionEnum.NO_LOGIN.getMsg())));
				return;
			}
		}
	}

	@Override
	public void destroy() {
		log.info("WhiteListFilter destroy...");
	}

	@SuppressWarnings(value="all")
	private boolean checkOem(String token,String oemCode,HttpServletResponse response) throws IOException{
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

		if(StringUtils.isNotBlank(token)) {
			refreshTokenOutTime(token, oemCode);
		}
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
			String outTime = redisService.get(RedisKey.LOGIN_TOKEN_KEY +"_yuncaiol_redisOutTime");
			if(StringUtils.isBlank(outTime)) {
				outTime = sysDictionaryService.getByCode("redis_token_outtime").getDictValue();
				redisService.set(RedisKey.LOGIN_TOKEN_KEY +"_yuncaiol_redisOutTime",outTime,600); //设置10分钟的失效时间
			}
			CurrUser currUser = JSON.parseObject(currUserJson, CurrUser.class);
			redisService.set(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + "userId_1_" + currUser.getUserId(),token,Integer.parseInt(outTime));
			redisService.set(RedisKey.LOGIN_TOKEN_KEY + oemCode + "_" + token,currUser,Integer.parseInt(outTime));
		}
	}
}
