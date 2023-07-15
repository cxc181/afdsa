package com.yuqian.itax.util.util.guojin;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuqian.itax.util.util.channel.MD5Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GuoJinUtil {
    //订单推送接口
    public static final String  ORDER_PUSHURL = "/order/orderRequest";
    //  服务商信息查询
    public static final  String CHANNEL_SERVICE_USER="/user/base/info";
    // 查询用户实名信息
    public static final String USER_AUTH_INFO = "/user/getUserAuthInfo";
    // 实名信息推送
    public static final String COMPANY_AUTH_PUSHURL = "user/saveUserAuth";
    // 自动登录国金
    public static final String LOGIN_TO_GJ = "user/userAutoLogin";
    // 用户注册
    public static final String USER_REGISTER = "user/register";

    /**
     *  订单推送国金，推送失败则重新推送。推送三次
     * @param dataParams 数据参数
     * @param channelCode 渠道编码
     * @param serkey 国金秘钥
     * @param url 国金请求地址
     * @param num 重复次数
     * @return
     */
    public static String gotoWJOrderPush(Map<String,Object> dataParams,String channelCode,String serkey,String url, int num){
        if(num<0){
            num = 3;
        }
        url = url+ ORDER_PUSHURL;
        JSONObject jsonObject = null;
        while(num>0){
            num --;
            jsonObject = gjChannel(dataParams, channelCode, serkey,url);
            if (jsonObject != null && !("-1".equals(jsonObject.getString("retCode")))){
                return jsonObject.getString("retCode");
            }else{
                try {
                    Thread.sleep(10*1000L);
                } catch (InterruptedException e) {}
            }
        }
        return "";
    }

    /**
     *  订单推送国金，推送失败则重新推送。推送三次
     * @param dataParams 数据参数
     * @param serkey 国金秘钥
     * @param url 国金请求地址
     * @param num 重复次数
     * @return
     */
    public static String gotoWJCompanyAuthPush(Map<String,Object> dataParams,String serkey,String url, int num, String channelCode){
        if(num<0){
            num = 3;
        }
        url = url+ COMPANY_AUTH_PUSHURL;
        JSONObject jsonObject = null;
        while(num>0){
            num --;
            jsonObject = gjChannel(dataParams, channelCode, serkey,url);
            if (jsonObject != null && !("-1".equals(jsonObject.getString("retCode")))){
                return jsonObject.getString("retCode");
            }else{
                try {
                    Thread.sleep(10*1000L);
                } catch (InterruptedException e) {}
            }
        }
        return "";
    }

    public static JSONObject getUserInfoToGuoJin(Map<String,Object> dataParams,String channelCode,String serkey,String url){
        url = url+ CHANNEL_SERVICE_USER;
        JSONObject jsonObject = null;
        jsonObject = gjChannel(dataParams, channelCode, serkey,url);
        return jsonObject;
    }

    /**
     * 调用国金助手接口
     * @param dataParams 数据参数
     * @param channelCode 渠道编码
     * @param serkey 国金秘钥
     * @param url 国金请求地址
     * @return
     */
    public static JSONObject gjChannel(Map<String,Object> dataParams,String channelCode,String serkey,String url){
        Map<String,String> headParams = new HashMap<String,String>();
        if (channelCode != null){
            headParams.put("oemCode",channelCode);
        }
        String sign =  MD5Utils.md5sign(dataParams,serkey);
        dataParams.put("sign",sign);
        String jsonParams = JSONUtil.toJsonStr(dataParams);
        HttpRequest httpRequest = HttpRequest.post(url);
        httpRequest.setConnectionTimeout(30000);
        httpRequest.setReadTimeout(30000);
        JSONObject jsonObject;
        try {
            log.info("----请求国金助手开始：url:"+url+",参数：" + JSONObject.toJSONString(dataParams));
            HttpResponse response = httpRequest.addHeaders(headParams).body(jsonParams, "application/json").charset("utf-8").execute();
            log.info("----国金助手返回数据结果：" + response.body());
            jsonObject = JSON.parseObject(response.body());
        }catch (Exception e){
            jsonObject = new JSONObject();
            jsonObject.put("retCode","-1");
            jsonObject.put("retMsg","请求上游失败，请稍后再试");
        }
        return jsonObject;
    }
}
