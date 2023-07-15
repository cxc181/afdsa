package com.yuqian.itax.api.filter;

import com.yuqian.itax.common.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @ClassName ParamsSignFilter
 * @Description 接口参数验签
 * @Author jiangni
 * @Date 2019/7/18
 * @Version 1.0
 */
@Component
@WebFilter(urlPatterns = "/*")
@Order(value = 2)
@Slf4j
public class ParamsSignFilter implements Filter {
    @Value("${request.uri.whitelist}")
    private String whiteUris;

    @Value("${isCheckSign}")
    private String isCheckSign;

    private RedisService redisService;

    private static List<String> whiteUriList;

    private int i = 0;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext sc = filterConfig.getServletContext();
        WebApplicationContext cxt = (WebApplicationContext)WebApplicationContextUtils.getWebApplicationContext(sc);

        if(cxt != null && cxt.getBean("redisService") != null && redisService == null){
            redisService = (RedisService) cxt.getBean("redisService");
        }
        log.debug("=============ParamsSignFilter初始化---------"+ ++i);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException  {
        chain.doFilter(request,response);
//        response.setCharacterEncoding("utf-8");
//        response.setContentType("application/json;charset=utf-8");
//        Object obj = request.getAttribute("isSignTime");
//        if(obj != null){
//            filterChain.doFilter(request,response);
//            return ;
//        }
//        if(whiteUriList == null){
//            whiteUris = whiteUris.replaceAll(" ","");
//            whiteUriList = Arrays.asList(whiteUris.split(","));
//        }
//
////        chain.doFilter(request, response);
//        if("false".equals(isCheckSign)){
//            filterChain.doFilter(request, response);
//            return ;
//        }
//        String uri = request.getRequestURI();
//        String paramsuri = uri.substring(uri.lastIndexOf("/"));
//
//        log.debug("---请求地址："+ paramsuri + "-----------"+whiteUriList.contains(paramsuri));
//        if(whiteUriList.contains(paramsuri)){
//            filterChain.doFilter(request, response);
//            return ;
//        }
//        String oemCode = request.getHeader("oemCode");
//        String token = request.getHeader("token");
//        String sign = request.getParameter("sign");
//        if(StringUtils.isEmpty(sign)){
//            response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.FAIL.getRetCode(),"sign不能为空!")));
//        }else {
//            if (StringUtils.isEmpty(oemCode)) {
//                response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.FAIL.getRetCode(), "未找到oemCode信息")));
//                return;
//            }
//
//            log.debug(request.getRequestURI() + "----" + request.getMethod() + "===============" + redisService + "-------------" + RedisKey.OEM_CODE_KEY + oemCode);
//            if (redisService == null) {
//                response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.SIGN_EXCEPTION.getRetCode(), "签名失败，请注意使用安全！")));
//                return;
//            }
//            log.debug(request.getMethod() + "===============" + redisService + "-------------" + RedisKey.OEM_CODE_KEY + oemCode);
//            String oemInfoStr = redisService.get(RedisKey.OEM_CODE_KEY + oemCode);
//            if (StringUtils.isEmpty(oemInfoStr)) {
//                response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.FAIL.getRetCode(), "未找到客户端信息")));
//                return;
//            }
//            OemEntity entity = JSON.parseObject(oemInfoStr,OemEntity.class);
//            StringBuilder builder = signStr(request);
//            builder.append(entity.getOemSecret());
//            if (StringUtils.isNotEmpty(token)) {
//                builder.append(token);
//            }
//            String newSign = Md5Util.MD5(builder.toString(),"UTF-8").toLowerCase();
//            log.info("签名前参数," + builder.toString() + "sign值：" + sign + ",签名后：" + newSign);
//            if (newSign.equals(sign)) {
//                request.setAttribute("isSignTime",System.currentTimeMillis());
//                super.doFilter(request,response,filterChain);
//                log.info("sign签名成功");
//            } else {
//                response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.SIGN_EXCEPTION.getRetCode(), "签名失败，请注意使用安全！")));
//                return;
//            }
//        }
    }


    @Override
    public void destroy() {
        log.info("ParamsSignFilter destroy...");
    }

    /**
     * 生成签名
     * @param request
     * @return
     */
    private StringBuilder signStr(HttpServletRequest request){
        Enumeration<String> enumeration = request.getParameterNames();
        List<String> paramsNames = new ArrayList<>();
        while(enumeration.hasMoreElements()){
            String paramName = enumeration.nextElement();
            if(!"sign".equals(paramName)) {
                paramsNames.add(paramName);
            }
        }
        Collections.sort(paramsNames);
        StringBuilder sb = new StringBuilder();
        paramsNames.forEach(vo -> {
           sb.append(request.getParameter(vo)) ;
        });
        return sb;
    }
}
