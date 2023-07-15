package com.yuqian.itax.util.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/31 11:05
 *  @Description: ServletRequest处理类：解决request.getInputStream()只能被读取一次的问题
 *  不能直接读取否则会导致业务接口无法获取到参数
 */
@Slf4j
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper{

    private final byte[] body;
    private Map<String , Object> params = new HashMap<String, Object>();
    private String  paramsString = "";

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = HttpUtil.getByteByStream(request.getInputStream());
    }

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request  ,String key,String iv) {
        super(request);
        params = handleRequestMap(request, key, iv);//这里将扩展参数写入参数表
        body = paramsString.getBytes();
        //重新写入
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byte[] bytes = paramsString.getBytes();
            byteArrayOutputStream.write(bytes, 0, bytes.length);
            byteArrayOutputStream.flush();
        }catch (Exception e){
        }
    }

    /**
     * 处理解密参数
     * @param request
     * @param key
     * @param iv
     * @return
     */
    private Map<String , Object> handleRequestMap(HttpServletRequest request,String key,String iv){
        Map<String , Object> map  = new HashMap<>();
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            if(StringUtils.isNotBlank(responseStrBuilder)){
               log.debug("BodyReaderHttpServletRequestWrapper未解密param "+responseStrBuilder);
                String parameter = AESEncryptUtil.desEncrypt(responseStrBuilder.toString(),key,iv);
                log.debug("BodyReaderHttpServletRequestWrapper已解密parameter "+parameter);
                paramsString = parameter;
                if(StringUtils.isNotBlank(parameter)) {
                    map = JSONObject.parseObject(parameter, Map.class);
                }
            }
//            JSONObject jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
//            if(jsonObject.containsKey("data")){
//                System.out.println("未解密param "+jsonObject.getString("data"));
//                String parameter = AESEncryptUtil.desEncrypt(jsonObject.getString("data"),key,iv);
//                System.out.println("已解密parameter "+parameter);
//                paramsString = parameter;
//                map =  JSONObject.parseObject(parameter,Map.class);
//            }
        }catch (Exception io){
           log.error("解密参数失败：{}",io.getMessage());
        }
        return map;
    }

    public Map<String , Object> getRequestMap(){
        return params;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }
}

