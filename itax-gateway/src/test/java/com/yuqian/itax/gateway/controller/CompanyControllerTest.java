package com.yuqian.itax.gateway.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yuqian.itax.gateway.ItaxGatewayBootstrap;
import com.yuqian.itax.user.entity.query.CompanyListApiQuery;
import com.yuqian.itax.util.util.BodyReaderHttpServletRequestWrapper;
import com.yuqian.itax.util.util.HttpUtil;
import com.yuqian.itax.util.util.JSONUtils;
import com.yuqian.itax.util.util.Md5Util;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ItaxGatewayBootstrap.class)
@AutoConfigureMockMvc
public class CompanyControllerTest {

    private static final String OEM_CODE_KEY = "oemCode";
    private static final String OEM_CODE = "YCS";
    private static final String OEM_SECRET = "30ba7120c42c4dabb5a9ab96f832adab";
    private static final String SIGN_KEY = "sign";
    private static final String MODULE_NAME = "company";
    private static final String LIST_URL = MODULE_NAME + "/getList";
    private static final String CANCEL_URL = MODULE_NAME + "/cancellation";
    private static final String INVOICE_URL = MODULE_NAME + "/invoice";
    private static final String INVOICE_TAX_CALC_URL = MODULE_NAME + "/invoiceTaxCalc";
    private static final String PATCH_BANK_WATER_URL = MODULE_NAME + "/patchBankWater";
    private static final String INVOICE_WATER_LIST_URL = MODULE_NAME + "/getInvWaterList";

    private static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;

    private static final MediaType GENERAL_MEDIA_TYPE = MediaType.parseMediaType("*/*");
    private static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON_UTF8.getType(), MediaType.APPLICATION_JSON_UTF8.getSubtype(), UTF8_CHARSET);
    private static final MediaType TEXT_MEDIA_TYPE = new MediaType(MediaType.TEXT_HTML.getType(), MediaType.TEXT_HTML.getSubtype(), UTF8_CHARSET);
    private static final MediaType DEFAULT_FORM_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_FORM_URLENCODED.getType(), MediaType.APPLICATION_FORM_URLENCODED.getSubtype(), UTF8_CHARSET);
    private static final MediaType MULTIPART_FORM_MEDIA_TYPE = new MediaType(MediaType.MULTIPART_FORM_DATA.getType(), MediaType.MULTIPART_FORM_DATA.getSubtype(), UTF8_CHARSET);

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    public void a_list() {
//        CompanyListApiQuery param = new CompanyListApiQuery();
//        param.setCompanyName("自动化字号");
//        String jsonParam = JSONUtil.toJsonStr(param);
        Map<String,Object> paramMap  = new HashMap<String,Object>();
        paramMap.put("companyName","自动化字号");

        String jsonParam = JSONObject.toJSONString(paramMap);

        // 组装sign
        StringBuilder builder = signJsonStr(jsonParam);
        builder.append(OEM_SECRET);
        String sign = Md5Util.MD5(builder.toString(),"UTF-8").toLowerCase();
        log.info("sign值：" + sign);

        // 设置header
        HttpHeaders headers = new HttpHeaders();
        headers.set(OEM_CODE_KEY, OEM_CODE);
        headers.set(SIGN_KEY, sign);

        // 发送请求
        RequestBuilder request = MockMvcRequestBuilders
                .post(LIST_URL)
                .content(jsonParam)
                .accept(JSON_MEDIA_TYPE)
                .contentType(JSON_MEDIA_TYPE)
                .headers(headers);

        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true));
    }


    /**
     * 生成签名(json参数)
     */
    private StringBuilder signJsonStr(String jsonStr) throws IOException {
        JSONObject jsonObj = JSONObject.parseObject(jsonStr);
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
