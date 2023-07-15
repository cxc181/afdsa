package com.yuqian.itax.yishui.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.yishui.YiShuiSupport;
import com.yuqian.itax.yishui.entity.ContractInfoReq;
import com.yuqian.itax.yishui.entity.ContractInfoResp;
import com.yuqian.itax.yishui.entity.YiShuiBaseResp;
import com.yuqian.itax.yishui.entity.YsMerConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 人员详情 ： http://doc.zhuoyankeji.com/web/#/13/57
 * @author Administrator
 */
@Slf4j
public class ContractInfoService extends YiShuiSupport {

    private static final String URI = "/Enterprise/contractInfo";

    public static ContractInfoResp exec(ContractInfoReq req, String token, YsMerConfig config){
        if(config.isMockFlag()){
            return mock(req,token,config);
        }
        ContractInfoResp resp = new ContractInfoResp();
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
            resp = JSONUtil.toBean(baseResp.getData(),ContractInfoResp.class);
            resp.setCode(baseResp.getCode());
            resp.setMsg(baseResp.getMsg());
            return resp;
        }catch (BusinessException be){
            resp.setCode(be.getErrorCode());
            resp.setMsg(be.getMessage());
            return resp;
        }catch (Exception e){
            log.error("查询人员信息异常",e);
            resp.setCode("EX999");
            resp.setMsg("查询人员信息异常，请稍后再试");
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
    private static ContractInfoResp mock(ContractInfoReq req, String token ,YsMerConfig config){
        //TODO  mock的时候，要啥自己拿（设置）啥
        String mockJo = "{\"bank_code\":\"6222031904000208736\",\"is_contract\":1,\"protocol_img\":\"\",\"cer_front_img\":\"https://testyishui.oss-cn-shenzhen.aliyuncs.com/water_07428b4ea33fb067dd62224721251d14.png\",\"open_id\":\"\",\"cer_reverse_img\":\"https://testyishui.oss-cn-shenzhen.aliyuncs.com/water_90fa0c69a1be60129a16cf6e435fe04b.png\",\"cer_code\":\"430524200206262933\",\"mobile\":\"17261391101\",\"contract_end_time\":0,\"is_auth\":1,\"contract_start_time\":0,\"sign_time\":1675819488,\"professional_sn\":\"P0284910\",\"professional_id\":284910,\"cer_face\":\"\",\"sign_img\":\"\",\"name\":\"彭金权\",\"contract_img\":\"\"}";
        ContractInfoResp resp = JSONUtil.toBean(mockJo,ContractInfoResp.class);
        resp.setCode("200");
        resp.setMsg("成功");
        return resp;
    }

    public static void main(String[] args) {
        YsMerConfig config = new YsMerConfig();
        String token = "sign_2f5b4ab39acb01306b9901fa47ec6c00";

        ContractInfoReq req = new ContractInfoReq();
        req.setEnterprise_professional_facilitator_id(238406);
        ContractInfoResp res = ContractInfoService.exec(req,token,config);
        System.out.println(JSONUtil.toJsonStr(res));
    }
}
