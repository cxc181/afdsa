package com.yuqian.itax.gateway.filter;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.agent.entity.OemAccessPartyEntity;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.query.OemAccessPartyQuery;
import com.yuqian.itax.agent.service.ApiRequestMessageRecordService;
import com.yuqian.itax.agent.service.OemAccessPartyService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.common.base.vo.ResultVo;
import com.yuqian.itax.common.constants.ErrorCodeEnum;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.constants.ResultConstants;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.util.exception.ExceptionEnum;
import com.yuqian.itax.util.util.*;
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
@Order(value = 3)
@Slf4j
public class AccessPartySignFilter implements Filter {

    @Value("${isCheckSign}")
    private String isCheckSign;

    private RedisService redisService;

    private OemService oemService;

    private OemAccessPartyService oemAccessPartyService;

    private ApiRequestMessageRecordService apiRequestMessageRecordService;

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
        if(cxt != null && cxt.getBean("oemAccessPartyService") != null && oemAccessPartyService == null){
            oemAccessPartyService = (OemAccessPartyService) cxt.getBean("oemAccessPartyService");
        }
        log.debug("=============ParamsSignFilter初始化---------"+ ++i);
    }

//    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException  {
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

        if("false".equals(isCheckSign)){
            filterChain.doFilter(requestWrapper, response);
            return ;
        }
        String uri = requestWrapper.getRequestURI();
        log.debug("---请求地址："+ uri);

        /**
         * 非接入方，走ParamsSignFilter默认的校验规则
         */
        if(uri.indexOf("/accessParty/") < 0 && uri.indexOf("/thirdPartyInvoice/")<0){
            filterChain.doFilter(requestWrapper, response);
            return ;
        }

        String contentType = requestWrapper.getHeader("Content-Type");
        if(StringUtils.isEmpty(contentType) || contentType.indexOf("application/json")<0){
            response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ErrorCodeEnum.REQUIRED_PARAM_NULL.getCode(),"Content-Type值错误或为空")));
            return ;
        }
        String oemCode = requestWrapper.getHeader("oemCode");  //机构编码
        String otherPayOemcode = requestWrapper.getHeader("otherPayOemcode");
        if(StringUtil.isNotBlank(otherPayOemcode)){
            oemCode= otherPayOemcode;
        }
        String accessPartyCode = requestWrapper.getHeader("accessPartyCode"); //接入方编码
        String sign = requestWrapper.getHeader("sign");
        if(StringUtils.isEmpty(sign)){
            response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.FAIL.getRetCode(),"身份识别标识sign不能为空!")));
        }else {
            if (StringUtils.isEmpty(accessPartyCode) || StringUtils.isEmpty(oemCode)) {
                response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.FAIL.getRetCode(), "未找到接入方信息")));
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

            //获取接入方信息
            OemAccessPartyEntity oemAccessPartyEntity = null;
            String accessPartyInfoStr = redisService.get(RedisKey.OEM_ACCESSPARTY_CODE_KEY + oemCode+"_"+accessPartyCode);
            if (StringUtils.isEmpty(accessPartyInfoStr)) {
                OemAccessPartyQuery oemAccessPartyQuery = new OemAccessPartyQuery();
                oemAccessPartyQuery.setAccessPartyCode(accessPartyCode);
                oemAccessPartyQuery.setOemCode(oemCode);
                oemAccessPartyQuery.setStatus(1);
                oemAccessPartyEntity = oemAccessPartyService.findByCode(oemAccessPartyQuery);
                if(oemAccessPartyEntity == null) {
                    response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ErrorCodeEnum.OEM_ACCESSPARTYCODE_ERROR.getCode(), ErrorCodeEnum.OEM_ACCESSPARTYCODE_ERROR.getText())));
                    return ;
                }else{
                    //设置10分钟的有效时间
                    redisService.set(RedisKey.OEM_ACCESSPARTY_CODE_KEY + oemCode+"_"+accessPartyCode,oemAccessPartyEntity,10*60);
                }
            }else {
                oemAccessPartyEntity = JSON.parseObject(accessPartyInfoStr, OemAccessPartyEntity.class);
            }

            if(oemAccessPartyEntity == null){
                response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ErrorCodeEnum.OEM_ACCESSPARTYCODE_ERROR.getCode(), ErrorCodeEnum.OEM_ACCESSPARTYCODE_ERROR.getText())));
                return ;
            }
            //设置加密参数
            String key = StringUtils.rightPad(oemCode,16,oemCode);
            String iv = StringUtils.rightPad(oemAccessPartyEntity.getAccessPartyCode(),16,"0");

            //参数解密
            BodyReaderHttpServletRequestWrapper bodyReaderHttpServletRequestWrapper= null;
            Map<String, Object> map = null;
            StringBuilder builder = null;
            if(!(uri.endsWith("/login") || uri.endsWith("/findRegisterOrder") || uri.endsWith("/findCompanyList") || uri.endsWith("/getSecretKey")
                    || uri.indexOf("/thirdPartyInvoice/")>=0 || uri.endsWith("/addPaymentVoucher") || uri.endsWith("/getBusinessScopeByParkId")
                    || uri.endsWith("/findParkAgreements") || uri.endsWith("/synchronousTaxCode") || uri.endsWith("/cancelRegisterOrder")
                    || uri.endsWith("/createRegisterOrder") || uri.endsWith("/getUsableParkIndustry") || uri.endsWith("/updateAuth")
                    || uri.endsWith("/getIndustryList") || uri.endsWith("/getIndustryBusinessScope"))) { //非登陆接口，走全参数加密
                bodyReaderHttpServletRequestWrapper = new BodyReaderHttpServletRequestWrapper(requestWrapper, key, iv);
                map = bodyReaderHttpServletRequestWrapper.getRequestMap();
                String token = requestWrapper.getHeader("token");
                if(StringUtil.isBlank(token)){
                    response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ExceptionEnum.NO_LOGIN.getCode(),"尚未登陆，请先登录")));
                }else{
                    map.put("token",token);
                }
                builder  = signJsonStr(map);
                builder.append(entity.getOemSecret()).append(oemAccessPartyEntity.getAccessPartySecret());
            }else{  //登陆接口，不加密
                bodyReaderHttpServletRequestWrapper = new BodyReaderHttpServletRequestWrapper(requestWrapper);
                JSONObject jsonObj = HttpUtil.getjsonParamByRequest(bodyReaderHttpServletRequestWrapper);
                if(jsonObj != null){
                    map = JSONObject.parseObject(jsonObj.toJSONString(),Map.class);
                }else{
                    map = new HashMap<>();
                }
                builder  = signJsonStr(map);
                builder.append(oemAccessPartyEntity.getAccessPartySecret());
            }
            String newSign = Md5Util.MD5(builder.toString(),"UTF-8").toLowerCase();
            log.info("接收参数： sign值：" + sign + "\n 签名后：" + newSign);

            // 获取请求原始业务参数报文
            if(map != null){
                if(map.containsKey("idCardFront")){
                    map.put("idCardFront","图片base64");
                }
                if(map.containsKey("idCardBack")){
                    map.put("idCardBack","图片base64");
                }
            }
            log.info("接收到业务参数原始请求报文：{}",null == map ? null :map.toString());

            if (newSign.equals(sign) || uri.endsWith("/getSecretKey")) {
                // 设置时间戳标识，防止重复验签
                request.setAttribute("isSignTime",System.currentTimeMillis());
                ResponseWrapper wrapper = new ResponseWrapper((HttpServletResponse) response);
                filterChain.doFilter(bodyReaderHttpServletRequestWrapper, wrapper);

                // 获取接口返回值
                String result = wrapper.getResponseData(response.getCharacterEncoding());
                log.info("sign签名成功,返回结果：{}",result);
                if(!(uri.endsWith("/login") || uri.endsWith("/findRegisterOrder") || uri.endsWith("/findCompanyList") || uri.endsWith("/createRegisterOrder")
                        || uri.indexOf("/thirdPartyInvoice/")>=0  || uri.endsWith("/addPaymentVoucher") || uri.endsWith("/getBusinessScopeByParkId")
                        || uri.endsWith("/getUsableParkIndustry") || uri.endsWith("/updateAuth") || uri.endsWith("/findParkAgreements") || uri.endsWith("/cancelRegisterOrder")
                        || uri.endsWith("/synchronousTaxCode") || uri.endsWith("/getIndustryList") || uri.endsWith("/getIndustryBusinessScope"))) { //非登陆接口，走全参数加密
                    //对返回的data数据进行加密开始 add ni.jiang
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if (jsonObject.containsKey("data") || jsonObject.get("data") != null) {
                        jsonObject.put("data", AESEncryptUtil.encrypt(JSONObject.toJSONString(jsonObject.get("data")), key, iv));
                    }
                    String resultData = JSONUtil.toJsonStr(jsonObject);
                    //对返回的data数据进行加密结束
                    response.setContentLength(resultData.getBytes().length);
                    response.getWriter().write(resultData);
                    response.getWriter().flush();
                }else{ //登陆接口，不加密
                    response.getWriter().write(result);// 重新写入response并返回
                    response.getWriter().flush();
                }

                // 过滤base64图片
                if (uri.endsWith("/thirdPartyQueryInvoice")) {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    JSONObject data = jsonObject.getJSONObject("data");
                    data.put("invoiceImgs", "发票图片");
                    data.put("invImgList", "发票图片列表");
                    result = data.toJSONString();
                }
                // 保存api接口请求报文记录lock
                this.apiRequestMessageRecordService.addRequestRecord(requestWrapper.getHeader("oemCode"), requestWrapper.getRequestURL().toString(),
                        requestWrapper.getHeader("sign"), requestWrapper.getHeader("version"),
                        result.replaceAll("\n","").replaceAll("\t",""), null == map ? null : JSONObject.toJSONString(map));
            } else {
                response.getWriter().write(JSONUtil.toJsonStr(new ResultVo(ResultConstants.SIGN_EXCEPTION.getRetCode(), "接口参数验签失败，请注意使用安全！")));
                // 保存api接口请求报文记录
                this.apiRequestMessageRecordService.addRequestRecord(requestWrapper.getHeader("oemCode"), requestWrapper.getRequestURL().toString(),
                        requestWrapper.getHeader("sign"), requestWrapper.getHeader("version"), "接口参数验签失败，请注意使用安全！", null == map ? null : JSONObject.toJSONString(map));
                return;
            }
        }
    }


    @Override
    public void destroy() {
        log.info("ParamsSignFilter destroy...");
    }

    /**
     * @Description 生成签名(json参数)
     * @Author  Kaven
     * @Date   2020/3/31 10:11
     * @Param   HttpServletRequest
     * @Return  StringBuilder
     * @Exception IOException
     */
    private StringBuilder signJsonStr(Map<String,Object> map){
        List<String> paramsNames = new ArrayList<>();
        Iterator iterator = map.entrySet().iterator();
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
            String valus = map.get(vo).toString();
            if(StringUtil.isNotBlank(valus)){
                valus = valus.trim();
            }
            sb.append(valus);
        });
        return sb;
    }

}
