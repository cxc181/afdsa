package com.yuqian.itax.yishui.service;

import cn.hutool.json.JSONUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.yishui.YiShuiSupport;
import com.yuqian.itax.yishui.entity.ContractSaveReq;
import com.yuqian.itax.yishui.entity.YiShuiBaseResp;
import com.yuqian.itax.yishui.entity.YsMerConfig;
import lombok.extern.slf4j.Slf4j;


/**
 * 第三方单号查询订单信息 ： http://doc.zhuoyankeji.com/web/#/13/63
 * @author Administrator
 */
@Slf4j
public class ContractSaveService extends YiShuiSupport {

    private static final String URI = "/Enterprise/contractSave";

    public static YiShuiBaseResp exec(ContractSaveReq req, String token, YsMerConfig config){
        if(config.isMockFlag()){
            return mock(req,token,config);
        }
        YiShuiBaseResp baseResp = null;
        try{
            baseResp = sendToYS(req,token,URI,config);
//            //失败情况下，直接返回，仅保留retCode和retMsg给业务即可
//            if(!Objects.equals(baseResp.getCode(),YiShuiBaseResp.SUCCESS)){
//                return baseResp;
//            }
            return baseResp;
        }catch (BusinessException be){
            baseResp.setCode(be.getErrorCode());
            baseResp.setMsg(be.getMessage());
            return baseResp;
        }catch (Exception e){
            log.error("修改完成人员信息异常",e);
            baseResp.setCode("EX999");
            baseResp.setMsg("修改完成人员信息异常，请稍后再试");
            return baseResp;
        }
    }

    /**
     * mock返回登陆token
     * @param req
     * @param config
     * @return
     */
    private static YiShuiBaseResp mock(ContractSaveReq req, String token, YsMerConfig config){
        YiShuiBaseResp resp = new YiShuiBaseResp();
        resp.setCode("200");
        resp.setMsg("成功");
        return resp;
    }

    /**
     * {"msg":"请求成功","code":"200","data":"YC20230208094700001"}
     * @param args
     */
    public static void main(String[] args) {
        String back = "https://inabei-public.oss-cn-hangzhou.aliyuncs.com/dev/404/17261391101_idcardback1663236194877.png";
        String front = "https://inabei-public.oss-cn-hangzhou.aliyuncs.com/dev/404/17261391101_idcardfront1663236078198.png";

        YsMerConfig config = new YsMerConfig();
        String token = "sign_ee64cfda01033cfb4486f83d185b02ac";

        ContractSaveReq req = new ContractSaveReq();
        req.setEnterprise_professional_facilitator_id(238405L);
        req.setName("彭金权");
        req.setCer_code("430524200206262933");
        req.setBank_code("6222031904000208736");
        req.setMobile("17261391101");
        req.setSign_img(front);
        req.setProtocol_img(front);
        req.setContract_img(front);
//        req.setCer_front_img(front);
//        req.setCer_reverse_img(back);

        YiShuiBaseResp res = ContractSaveService.exec(req,token,config);
        System.out.println(JSONUtil.toJsonStr(res));
    }
}
