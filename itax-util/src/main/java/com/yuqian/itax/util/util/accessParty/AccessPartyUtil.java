package com.yuqian.itax.util.util.accessParty;

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
public class AccessPartyUtil {
    //订单推送接口
    public static final String  INVOICE_ORDER_PUSH = "/thirdPartyInvoice/call";

    /**
     *  订单推送第三方，推送失败则重新推送。推送三次
     * @param dataParams 数据参数
     * @param billsCode 快开机构编码
     * @param secretKey 第三方秘钥
     * @param url 请求地址
     * @param num 重复次数
     * @return
     */
    public static String gotoOrderPush(Map<String,Object> dataParams,String billsCode,String secretKey,String url, int num){
        if(num<0){
            num = 3;
        }
        url = url+ INVOICE_ORDER_PUSH;
        JSONObject jsonObject = null;
        while(num>0){
            num --;
            jsonObject = thirdParty(dataParams, billsCode, secretKey, url);
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
     * 调用第三方接口
     * @param dataParams 数据参数
     * @param billsCode 快开机构编码
     * @param secretKey 第三方秘钥
     * @param url 请求地址
     * @return
     */
    public static JSONObject thirdParty(Map<String,Object> dataParams, String billsCode, String secretKey, String url){
        Map<String,String> headParams = new HashMap<String,String>();
        headParams.put("oemCode",billsCode);
        String sign =  MD5Utils.md5sign(dataParams,secretKey);
        dataParams.put("sign",sign);
        String jsonParams = JSONUtil.toJsonStr(dataParams);
        HttpRequest httpRequest = HttpRequest.post(url);
        httpRequest.setConnectionTimeout(30000);
        httpRequest.setReadTimeout(30000);
        JSONObject jsonObject;
        try {
            log.info("----请求第三方回调接口开始：url:"+url+",参数：" + JSONObject.toJSONString(dataParams));
            HttpResponse response = httpRequest.addHeaders(headParams).body(jsonParams, "application/json").charset("utf-8").execute();
            log.info("----第三方返回数据结果：" + response.body());
            jsonObject = JSON.parseObject(response.body());
        }catch (Exception e){
            System.out.println("失败信息:"+e.getMessage());
            jsonObject = new JSONObject();
            jsonObject.put("retCode","-1");
            jsonObject.put("retMsg","请求第三方失败，请稍后再试");
        }
        return jsonObject;
    }
}
