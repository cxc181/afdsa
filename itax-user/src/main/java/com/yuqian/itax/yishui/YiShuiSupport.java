package com.yuqian.itax.yishui;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.util.util.yishui.AESUtils;
import com.yuqian.itax.yishui.entity.YiShuiBaseResp;
import com.yuqian.itax.yishui.entity.YsMerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import java.util.Objects;

/**
 * 易税灵工API对接通讯服务
 *
 * @author Administrator
 */
@Slf4j
public class YiShuiSupport {

    /**
     * 发送请求到易税灵工
     *
     * @param busiObj - 业务接口参数对象
     * @param token   - 登陆token
     * @param config  - 商户配置
     * @return
     */
    public static YiShuiBaseResp sendToYS(Object busiObj, String token, String uri, YsMerConfig config) throws BusinessException {
        if (config.isSideFlag()) {
            return YiShuiBaffleSupport.sendToBaffle(busiObj, token, uri, config);
        }
        String url = config.getDomain() + uri;
        log.info("业务请求url:" + url);
        //1.处理业务数据处理
        JSONObject requestJo = new JSONObject();
        requestJo.put("token", token);
        requestJo.put("data", busiObj);
        log.info("业务请求参数:" + JSONUtil.toJsonPrettyStr(requestJo));
        String response = null;
        try {

            HttpResponse httpResponse = HttpRequest.post(url).body(JSONUtil.toJsonStr(requestJo))
                    .timeout(10000).contentType(ContentType.JSON.toString()).execute();
            int httpStatus = httpResponse.getStatus();
            if (httpStatus != HttpStatus.SC_OK) {
                log.error("业务请求处理失败,HttpStatus:" + httpStatus);
                throw new BusinessException("业务请求失败,httpStatus:" + httpStatus);
            }
            response = httpResponse.body();
            if (!JSONUtil.isJson(response)) {
                log.error("业务请求处理失败,返回数据不是JSON格式，response：{}", response);
                throw new BusinessException("业务请求失败,上游返回数据格式异常");
            }
            log.info("业务请求返回数据:" + JSONUtil.toJsonPrettyStr(response));
        } catch (BusinessException be) {
            throw new BusinessException(Integer.valueOf(YiShuiBaseResp.UNKNOWN_ERR), be.getLocalizedMessage());
        } catch (Exception e) {
            log.error("业务请求处理失败,请求上游接口异常", e);
            throw new BusinessException(Integer.valueOf(YiShuiBaseResp.UNKNOWN_ERR), "上游接口异常，请稍后再试");
        }
        JSONObject resJO = JSONUtil.parseObj(response);
        Integer code = resJO.getInt("code");
        String msg = resJO.getStr("msg");
        YiShuiBaseResp baseResp = new YiShuiBaseResp();
        baseResp.setCode(code + "");
        baseResp.setMsg(msg);
        if (Objects.equals("200", baseResp.getCode())) {
            //数据解密
            try {
                String data = resJO.getStr("data");
                if (!JSONUtil.isJson(data)) {
                    data = AESUtils.decrypt(data, config.getAseKey());
                }
                baseResp.setData(data);
            } catch (Exception e) {
                throw new BusinessException(Integer.valueOf(YiShuiBaseResp.UNKNOWN_ERR), "上游接口异常，请稍后再试");
            }
        }
        return baseResp;
    }
}
