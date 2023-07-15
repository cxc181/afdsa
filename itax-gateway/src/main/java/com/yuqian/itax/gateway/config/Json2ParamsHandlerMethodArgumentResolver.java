package com.yuqian.itax.gateway.config;

/**
 * @ClassName Json2ParamsHandlerMethodArgumentResolver
 * @Description
 * @Author jiangni
 * @Date 2019/9/7
 * @Version 1.0
 */
import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.gateway.annotation.JsonParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数转换（POST,默认前端以JSON形式提交）
 */
@Slf4j
@SuppressWarnings("all")
public class Json2ParamsHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * key
     */
    private static final String JSON_BODY_KEY = "JSON_BODY_KEY";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonParam.class);
    }


    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 获取jsonNode
        JsonNode jsonNode = getJsonBody(webRequest);
        if(jsonNode == null){
            return null;
        }

        JsonParam requestSingleParam = parameter.getParameterAnnotation(JsonParam.class);
        // 名
        String value = determineParamName(parameter, requestSingleParam);
        // 结果
        JsonNode paramValue = jsonNode.get(value);
        // 是否必须
        boolean require = requestSingleParam.require();
        if (paramValue == null && require) {
            throw new ServerException("参数[" + value + "]不能为空。");
        }

        if (paramValue == null) {
            return null;
        }
        Class<?> parameterType = parameter.getParameterType();
        Constructor<?> constructor = parameterType.getConstructor(String.class);
        Object param = null;
        try {
            param = constructor.newInstance(paramValue.asText());
        } catch (Exception e) {
            log.error("bind method parameters error", e);
            throw new BusinessException("参数[" + value + "] 格式转换错误，传递参数为：[" + paramValue.toString() + "]");
        }
        return param;
    }

    private String determineParamName(MethodParameter parameter, JsonParam requestSingleParam) {
        String value = requestSingleParam.value();
        if (StringUtils.isEmpty(value)) {
            value = parameter.getParameterName();
        }
        return value;
    }

    private JsonNode getJsonBody(NativeWebRequest webRequest) throws IOException {
        // 有就直接获取
        String jsonBody = (String) webRequest.getAttribute(JSON_BODY_KEY, NativeWebRequest.SCOPE_REQUEST);
        // 没有就从请求中读取
        if (jsonBody == null) {
            try {
                HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
                if("post".equalsIgnoreCase(servletRequest.getMethod())) {
                    jsonBody = IOUtils.toString(servletRequest.getReader());
                    webRequest.setAttribute(JSON_BODY_KEY, jsonBody, NativeWebRequest.SCOPE_REQUEST);

                    //数据转换
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readTree(jsonBody);
                }else if("get".equalsIgnoreCase(servletRequest.getMethod())){
                    Map<String,Object> params = new HashMap<>();
                    servletRequest.getParameterMap().forEach((k,v)->{
                        params.put(k,v[0]);
                    });
                    //数据转换
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readTree(JSONUtils.toJSONString(params));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //数据转换
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonBody);
    }

}