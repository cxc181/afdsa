package com.yuqian.itax.yishui.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.yishui.YiShuiSupport;
import com.yuqian.itax.yishui.entity.QuerySignResultReq;
import com.yuqian.itax.yishui.entity.QuerySignResultResp;
import com.yuqian.itax.yishui.entity.YiShuiBaseResp;
import com.yuqian.itax.yishui.entity.YsMerConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 查询电子签约结果 ： http://doc.zhuoyankeji.com/web/#/13/349
 * @author Administrator
 */
@Slf4j
public class QuerySignResultService extends YiShuiSupport {

    private static final String URI = "/Enterprise/querySignResult";

    public static QuerySignResultResp exec(QuerySignResultReq req, String token, YsMerConfig config){
        if(config.isMockFlag()){
            return mock(req,token,config);
        }
        QuerySignResultResp resp = new QuerySignResultResp();
        YiShuiBaseResp baseResp = null;
        try{
            baseResp = sendToYS(req,token,URI,config);
            BeanUtil.copyProperties(baseResp,resp);
            //失败情况下，直接返回，仅保留retCode和retMsg给业务即可
            if(!Objects.equals(resp.getCode(),YiShuiBaseResp.SUCCESS)){
                return resp;
            }
            //调用成功，进行参数赋值
            resp.setData(null);
            resp = JSONUtil.toBean(baseResp.getData(),QuerySignResultResp.class);
            resp.setCode(baseResp.getCode());
            resp.setMsg(baseResp.getMsg());
            return resp;
        }catch (BusinessException be){
            resp.setCode(be.getErrorCode());
            resp.setMsg(be.getMessage());
            return resp;
        }catch (Exception e){
            log.error("查询电子签结果异常",e);
            resp.setCode("EX999");
            resp.setMsg("查询电子签结果异常，请稍后再试");
            return resp;
        }
    }

    /**
     * mock返回登陆token
     * @param req
     * @param token
     * @param config
     * @return
     */
    private static QuerySignResultResp mock(QuerySignResultReq req, String token,YsMerConfig config){
        QuerySignResultResp resp = new QuerySignResultResp();
        resp.setCode("200");
        resp.setMsg("成功");
        //TODO
        resp.setResultCode(1);
        return resp;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        YsMerConfig config = new YsMerConfig();
        String token = "sign_4d6374c68e43f5de180f7f7bdc0f8d9c";
        QuerySignResultReq req = new QuerySignResultReq();
        req.setTransactionCode("");
        QuerySignResultResp res = QuerySignResultService.exec(req,token,config);
        System.out.println(JSONUtil.toJsonStr(res));
    }
}
