package com.yuqian.itax.yishui.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.yishui.entity.EnterpriseLoginReq;
import com.yuqian.itax.yishui.entity.YsMerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import java.net.URLEncoder;
import java.util.*;

/**
 * 新版API登陆 ： http://doc.zhuoyankeji.com/web/#/13/254
 * @author Administrator
 */
@Slf4j
public class EnterpriseLoginService {

    private static final String URI = "/sdk/v1/login";

    public static String exec(EnterpriseLoginReq req, YsMerConfig config){
        if(config.isMockFlag()){
            return mock(req,config);
        }
        try{
            //1.参数进行签名
            Map<String, Object> paramMap = BeanUtil.beanToMap(req);
            String sign = md5sign(paramMap,config.getSecret());
            paramMap.put("sign",sign);
            //2.发起请求到易税
            String url = config.getDomain() + URI;
            log.info("【请求易税】url{}，param{}",url,paramMap.toString());
            HttpResponse httpResponse = HttpRequest.post(url).body(JSON.toJSONString(paramMap))
                    .timeout(10000).contentType(ContentType.JSON.toString()).execute();
            int httpStatus = httpResponse.getStatus();
            if(httpStatus != HttpStatus.SC_OK){
                log.error("企业用户["+config.getUserName()+"]登陆异常,HttpStatus:"+httpStatus);
                throw new BusinessException("业务请求获取token失败,httpStatus:"+httpStatus);
            }
            String response = httpResponse.body();
            log.info("企业用户["+config.getUserName()+"]登陆返回数据:"+response);
            if(!JSONUtil.isJson(response)){
                log.error("业务请求["+config.getUserName()+"]处理失败,返回数据不是JSON格式");
                throw new BusinessException("企业用户["+config.getUserName()+"]登陆失败,上游返回数据格式异常");
            }
            JSONObject resJO = JSONUtil.parseObj(response);
            Integer code = resJO.getInt("code");
            String msg = resJO.getStr("msg");
            if(200 != code){
                throw new BusinessException(msg);
            }
            String token = resJO.getStr("token");
            if(StrUtil.isBlank(token)){
                throw new BusinessException("用户登陆获取token异常");
            }
            return token;
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            throw new BusinessException("用户登陆获取token异常" + e.getMessage());
        }
    }

    /**
     * MD5签名
     *  1、对参数进行自然排序
     *  2、参数以“参数1=参数1值&参数2=参数2值...”的方式进行拼接
     *  3、在第二步的基础上拼接&secret=secret值
     *  4、进行md5加密
     * @param params
     * @param secret
     * @return
     */
    private static String md5sign(Map<String, Object> params, String secret) throws Exception{
        List<String> keys = Arrays.asList(params.keySet().toArray(new String[params.size()]));
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        Iterator<String> var4 = keys.iterator();
        while(var4.hasNext()) {
            String key = var4.next();
            Object value = params.get(key);
            String strValue = "";
            if (value != null) {
                strValue = (String)value;
                //用户名可能为中文，进行urlencoder
                if(Objects.equals("user_name",key)){
                    strValue = URLEncoder.encode((String)value,"UTF-8");
                }
                sb.append(key).append("=").append(strValue.trim()).append("&");
            }
        }
        sb.append("secret").append("=").append(secret);
        System.out.println("待签名数据：" + sb.toString());
        return DigestUtil.md5Hex(sb.toString());
    }

    /**
     * mock返回登陆token
     * @param req
     * @param config
     * @return
     */
    private static String mock(EnterpriseLoginReq req, YsMerConfig config){
        return "token"+System.currentTimeMillis();
    }


    public static void main(String[] args) throws Exception{
        YsMerConfig config = new YsMerConfig();
        EnterpriseLoginReq req = new EnterpriseLoginReq();
        req.setUser_name(config.getUserName());
        req.setPassword(config.getPassword());
        req.setTimestamp(System.currentTimeMillis()+"");
        String token = EnterpriseLoginService.exec(req,config);
        System.out.println(token);
    }
}
