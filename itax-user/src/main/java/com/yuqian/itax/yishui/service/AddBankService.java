package com.yuqian.itax.yishui.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.yishui.YiShuiSupport;
import com.yuqian.itax.yishui.entity.AddBankReq;
import com.yuqian.itax.yishui.entity.YiShuiBaseResp;
import com.yuqian.itax.yishui.entity.YsMerConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 签约管理/新增收款账户 ： http://doc.zhuoyankeji.com/web/#/13/248
 * @author Administrator
 */
@Slf4j
public class AddBankService extends YiShuiSupport {

    private static final String URI = "/Enterprise/addBank";

    public static YiShuiBaseResp exec(AddBankReq req, String token, YsMerConfig config){
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
            //调用成功，进行参数赋值
            JSONObject dataJo = JSONUtil.parseObj(baseResp.getData());
            baseResp.setData(dataJo.getInt("professional_bank_id")+"");
            return baseResp;
        }catch (BusinessException be){
            baseResp.setCode(be.getErrorCode());
            baseResp.setMsg(be.getMessage());
            return baseResp;
        }catch (Exception e){
            log.error("添加结算银行卡异常",e);
            baseResp.setCode("EX999");
            baseResp.setMsg("添加结算银行卡异常，请稍后再试");
            return baseResp;
        }
    }

    /**
     * mock返回银行卡ID
     * @param req
     * @param config
     * @return
     */
    private static YiShuiBaseResp mock(AddBankReq req, String token, YsMerConfig config){
        YiShuiBaseResp resp = new YiShuiBaseResp();
        resp.setCode("200");
        resp.setMsg("成功");
        resp.setData("76484");
        return resp;
    }

    /**
     * {"msg":"请求成功","code":"200","data":"YC20230208094700001"}
     * @param args
     */
    public static void main(String[] args) {
        YsMerConfig config = new YsMerConfig();
        String token = "sign_665ca19ec229ac90391712aa03e5181e";

        AddBankReq req = new AddBankReq();
        req.setName("程慧云");
        req.setBank_code("6216697500003503467");
        req.setMobile("18873685277");
        req.setProfessional_id(284913L);

        YiShuiBaseResp res = AddBankService.exec(req,token,config);
        System.out.println(JSONUtil.toJsonStr(res));
    }
}
