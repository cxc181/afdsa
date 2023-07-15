package com.yuqian.itax.yishui.service;

import cn.hutool.json.JSONUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.yishui.YiShuiSupport;
import com.yuqian.itax.yishui.entity.ChangeOrderStatusReq;
import com.yuqian.itax.yishui.entity.YiShuiBaseResp;
import com.yuqian.itax.yishui.entity.YsMerConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 批次审核 ： http://doc.zhuoyankeji.com/web/#/13/79
 *  -- 请求参数批次订单ID必须通过回调才能拿到
 * @author Administrator
 */
@Slf4j
public class ChangeOrderStatusService extends YiShuiSupport {

    private static final String URI = "/Enterprise/changeOrderStatus";

    public static YiShuiBaseResp exec(ChangeOrderStatusReq req, String token, YsMerConfig config){
        if(config.isMockFlag()){
            return mock(req,token,config);
        }
        YiShuiBaseResp baseResp = null;
        try{
            baseResp = sendToYS(req,token,URI,config);
            //失败情况下，直接返回，仅保留retCode和retMsg给业务即可
            if(!Objects.equals(baseResp.getCode(),YiShuiBaseResp.SUCCESS)){
                return baseResp;
            }
            return baseResp;
        }catch (BusinessException be){
            baseResp.setCode(be.getErrorCode());
            baseResp.setMsg(be.getMessage());
            return baseResp;
        }catch (Exception e){
            log.error("批次审核异常",e);
            baseResp.setCode("EX999");
            baseResp.setMsg("批次审核异常，请稍后再试");
            return baseResp;
        }
    }

    /**
     * mock返回登陆token
     * @param req
     * @param token
     * @param config
     * @return
     */
    private static YiShuiBaseResp mock(ChangeOrderStatusReq req, String token,YsMerConfig config){
        YiShuiBaseResp resp = new YiShuiBaseResp();
        resp.setCode("200");
        resp.setMsg("成功");
        //TODO
        return resp;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        YsMerConfig config = new YsMerConfig();
        String token = "sign_ee64cfda01033cfb4486f83d185b02ac";
        ChangeOrderStatusReq req = new ChangeOrderStatusReq();
        req.setEnterprise_order_id("65104");
        req.setStatus(1);
        req.setRemarks("通过");
        req.setApply_img("https://inabei-public.oss-cn-hangzhou.aliyuncs.com/dev/404/17261391101_idcardback1663236194877.png");
        YiShuiBaseResp res = ChangeOrderStatusService.exec(req,token,config);
        System.out.println(JSONUtil.toJsonStr(res));
    }
}
