package com.yuqian.itax.gateway.filter;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.service.ApiRequestMessageRecordService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.util.util.BodyReaderHttpServletRequestWrapper;
import com.yuqian.itax.util.util.HttpUtil;
import com.yuqian.itax.util.util.Md5Util;
import com.yuqian.itax.util.util.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/31 15:30
 *  @Description: 接口参数验签
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

    private OemService oemService;

    private ApiRequestMessageRecordService apiRequestMessageRecordService;

    private static List<String> whiteUriList;

    private int i = 0;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext sc = filterConfig.getServletContext();
        WebApplicationContext cxt = WebApplicationContextUtils.getWebApplicationContext(sc);

        if(cxt != null && cxt.getBean("redisService") != null && redisService == null){
            redisService = (RedisService) cxt.getBean("redisService");
        }
        if(cxt != null && cxt.getBean("oemService") != null && oemService == null){
            oemService = (OemService) cxt.getBean("oemService");
        }
        if(cxt != null && cxt.getBean("apiRequestMessageRecordService") != null && apiRequestMessageRecordService == null){
            apiRequestMessageRecordService = (ApiRequestMessageRecordService) cxt.getBean("apiRequestMessageRecordService");
        }
        log.debug("=============ParamsSignFilter初始化---------"+ ++i);
    }

//    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException  {
        // chain.doFilter(request,response);
        HttpServletRequest requestWrapper = null;
        if(request instanceof HttpServletRequest) {
            try {
                requestWrapper = new BodyReaderHttpServletRequestWrapper((HttpServletRequest) request);
            }catch (Exception e){
                requestWrapper = (HttpServletRequest)request;
            }
        }else{
            requestWrapper = (HttpServletRequest)request;
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");

        // 验证请求是否已经验签
        Object obj = request.getAttribute("isSignTime");
        if(obj != null){
            filterChain.doFilter(requestWrapper, response);
            return ;
        }

        if(whiteUriList == null){
            whiteUris = whiteUris.replaceAll(" ","");
            whiteUriList = Arrays.asList(whiteUris.split(","));
        }

        if("false".equals(isCheckSign)){
            filterChain.doFilter(requestWrapper, response);
            return ;
        }
        String uri = requestWrapper.getRequestURI();
        String paramsuri = uri.substring(uri.lastIndexOf("/"));

        log.debug("---请求地址："+ paramsuri + "-----------" + whiteUriList.contains(paramsuri));

        if(whiteUriList.contains(paramsuri)){
            filterChain.doFilter(requestWrapper, response);
            return ;
        }

        /**
         * 接入方 通过AccessPartySignFilter 进行校验
         */
        if(uri.indexOf("/accessParty/")>=0 || uri.indexOf("/thirdPartyInvoice/")>=0){
            filterChain.doFilter(requestWrapper, response);
            return ;
        }

        String contentType = requestWrapper.getHeader("Content-Type");
        if(StringUtils.isEmpty(contentType) || contentType.indexOf("application/json")<0){
            response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ErrorCodeEnum.REQUIRED_PARAM_NULL.getCode(),"Content-Type值错误或为空")));
            return ;
        }
        String oemCode = requestWrapper.getHeader("oemCode");
        // String token = httpRequest.getHeader("token");
        String sign = requestWrapper.getHeader("sign");
        if(StringUtils.isEmpty(sign) && uri.indexOf("/getOemSecret") < 0){
            response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.FAIL.getRetCode(),"身份识别标识sign不能为空!")));
        }else {
            if (StringUtils.isEmpty(oemCode)) {
                response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.FAIL.getRetCode(), "未找到oemCode信息")));
                return;
            }
            log.debug(requestWrapper.getRequestURI() + "----" + requestWrapper.getMethod() + "===============" + redisService + "-------------" + RedisKey.OEM_CODE_KEY + oemCode);
            if (redisService == null) {
                response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.SIGN_EXCEPTION.getRetCode(), "接口参数验签失败，请注意使用安全！")));
                return;
            }
            log.debug(requestWrapper.getMethod() + "===============" + redisService + "-------------" + RedisKey.OEM_CODE_KEY + oemCode);
            OemEntity entity = null;
            String oemInfoStr = redisService.get(RedisKey.OEM_CODE_KEY + oemCode);
            if (StringUtils.isEmpty(oemInfoStr)) {
                entity = oemService.getOem(oemCode);
                if(entity == null) {
                    response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ErrorCodeEnum.OEMCODE_ERROR.getCode(), ErrorCodeEnum.OEMCODE_ERROR.getText())));
                    return ;
                }else{
                    redisService.set(RedisKey.OEM_CODE_KEY + entity.getOemCode(),entity);
                    if(entity.getOemStatus() != 1){
                        response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.SERVICE_EXPIRATION.getRetCode(), "已暂停服务，请联系客服确认！")));
                        return;
                    }
                }
            }else {
                entity = JSON.parseObject(oemInfoStr, OemEntity.class);
            }

            StringBuilder builder = signJsonStr(requestWrapper);
            builder.append(entity.getOemSecret());
            /*if (StringUtils.isNotEmpty(token)) {
                builder.append(token);
            }*/
            String newSign = Md5Util.MD5(builder.toString(),"UTF-8").toLowerCase();
            log.info("接收参数： sign值：" + sign + "\n 签名后：" + newSign);

            // 获取请求原始业务参数报文
            JSONObject jsonObj = HttpUtil.getjsonParamByRequest((BodyReaderHttpServletRequestWrapper) requestWrapper);
            if(jsonObj != null){
                if(jsonObj.containsKey("idCardFront")){
                    jsonObj.put("idCardFront","图片base64");
                }
                if(jsonObj.containsKey("idCardBack")){
                    jsonObj.put("idCardBack","图片base64");
                }
            }
            log.info("接收到业务参数原始请求报文：{}",null == jsonObj ? null :jsonObj.toString());

            if (newSign.equals(sign) || uri.indexOf("/getOemSecret") >= 0) {
                // 设置时间戳标识，防止重复验签
                request.setAttribute("isSignTime",System.currentTimeMillis());
                ResponseWrapper wrapper = new ResponseWrapper((HttpServletResponse) response);
                filterChain.doFilter(requestWrapper, wrapper);
                log.info("sign签名成功");

                // 获取接口返回值
                String result = wrapper.getResponseData(response.getCharacterEncoding());
                response.getOutputStream().write(result.getBytes());// 重新写入response并返回

                // 保存api接口请求报文记录lock
                this.apiRequestMessageRecordService.addRequestRecord(requestWrapper.getHeader("oemCode"), requestWrapper.getRequestURL().toString(),
                        requestWrapper.getHeader("sign"), requestWrapper.getHeader("version"),
                        result.replaceAll("\n","").replaceAll("\t",""), null == jsonObj ? null :jsonObj.toString());
            } else {
                response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.SIGN_EXCEPTION.getRetCode(), "接口参数验签失败，请注意使用安全！")));
                // 保存api接口请求报文记录
                this.apiRequestMessageRecordService.addRequestRecord(requestWrapper.getHeader("oemCode"), requestWrapper.getRequestURL().toString(),
                        requestWrapper.getHeader("sign"), requestWrapper.getHeader("version"), "接口参数验签失败，请注意使用安全！", null == jsonObj ? null :jsonObj.toString());
                return;
            }
        }
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
        if(enumeration==null){
            return new StringBuilder();
        }
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

    /**
     * @Description 生成签名(json参数)
     * @Author  Kaven
     * @Date   2020/3/31 10:11
     * @Param   HttpServletRequest
     * @Return  StringBuilder
     * @Exception IOException
     */
    private StringBuilder signJsonStr(HttpServletRequest requestWapper) throws IOException {
        BodyReaderHttpServletRequestWrapper request = (BodyReaderHttpServletRequestWrapper)requestWapper;
        JSONObject jsonObj = HttpUtil.getjsonParamByRequest(request);
        List<String> paramsNames = new ArrayList<>();
        if(jsonObj==null){
            return new StringBuilder();
        }
        Iterator iterator = jsonObj.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String paramName = entry.getKey().toString();
            if(!"sign".equals(paramName)) {
                paramsNames.add(paramName);
            }
        }
        Collections.sort(paramsNames);
        StringBuilder sb = new StringBuilder();
        paramsNames.forEach(vo -> {
            sb.append(jsonObj.getString(vo));
        });
        return sb;
    }

}
