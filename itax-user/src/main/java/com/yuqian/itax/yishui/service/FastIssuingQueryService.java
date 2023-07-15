package com.yuqian.itax.yishui.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.yishui.YiShuiSupport;
import com.yuqian.itax.yishui.entity.FastIssuingQueryReq;
import com.yuqian.itax.yishui.entity.FastIssuingQueryResp;
import com.yuqian.itax.yishui.entity.YiShuiBaseResp;
import com.yuqian.itax.yishui.entity.YsMerConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 第三方单号查询订单信息 ： http://doc.zhuoyankeji.com/web/#/13/63
 * @author Administrator
 */
@Slf4j
public class FastIssuingQueryService extends YiShuiSupport {

    private static final String URI = "/Enterprise/findOrderFromRequestNo";

    public static FastIssuingQueryResp exec(FastIssuingQueryReq req, String token, YsMerConfig config){
        if(config.isMockFlag()){
            return mock(req,token,config);
        }
        FastIssuingQueryResp resp = new FastIssuingQueryResp();
        YiShuiBaseResp baseResp = null;
        try{
            baseResp = sendToYS(req,token,URI,config);
            BeanUtil.copyProperties(baseResp,resp);
            //失败情况下，直接返回，仅保留retCode和retMsg给业务即可
            if(!Objects.equals(resp.getCode(),YiShuiBaseResp.SUCCESS)){
                return resp;
            }
            //调用成功，进行参数赋值
            resp = JSONUtil.toBean(baseResp.getData(),FastIssuingQueryResp.class);
            resp.setCode(baseResp.getCode());
            resp.setMsg(baseResp.getMsg());
            return resp;
        }catch (BusinessException be){
            resp.setCode(be.getErrorCode());
            resp.setMsg(be.getMessage());
            return resp;
        }catch (Exception e){
            log.error("查询付款结果异常",e);
            resp.setCode("EX999");
            resp.setMsg("查询付款结果异常，请稍后再试");
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
    private static FastIssuingQueryResp mock(FastIssuingQueryReq req, String token ,YsMerConfig config){
        //TODO  mock的时候，要啥自己拿（设置）啥
        String mockJo = "{\"msg\":\"请求成功\",\"code\":\"200\",\"data\":\"{\\\"enterprise_order_ext_id\\\":436400,\\\"enterprise_order_ext_sn\\\":\\\"BS23020816112002849101\\\",\\\"enterprise_order_id\\\":65098,\\\"enterprise_id\\\":1957,\\\"facilitator_id\\\":1,\\\"agent_id\\\":0,\\\"group_id\\\":0,\\\"client_manager_id\\\":0,\\\"client_id\\\":284910,\\\"client_sn\\\":\\\"P0284910\\\",\\\"client_name\\\":\\\"彭金权\\\",\\\"client_type\\\":1,\\\"real_money\\\":\\\"100.00\\\",\\\"plat_money\\\":\\\"104.94\\\",\\\"process_rate\\\":\\\"0.050000\\\",\\\"process_commission\\\":\\\"4.94\\\",\\\"process_diff\\\":\\\"0.00\\\",\\\"total_money\\\":\\\"100.00\\\",\\\"pre_balance\\\":\\\"0.00\\\",\\\"after_balance\\\":\\\"0.00\\\",\\\"op_type\\\":2,\\\"pay_sn\\\":\\\"\\\",\\\"pay_time\\\":0,\\\"pay_type\\\":3,\\\"status\\\":0,\\\"charge_status\\\":0,\\\"payment_status\\\":0,\\\"pay_code_back\\\":\\\"\\\",\\\"pay_err_msg\\\":\\\"\\\",\\\"remark\\\":\\\"佣金提成\\\",\\\"bank_code\\\":\\\"6222031904000208736\\\",\\\"check_time\\\":0,\\\"created_time\\\":1675821061,\\\"updated_time\\\":1675821061,\\\"deduction_mode\\\":1,\\\"mobile\\\":\\\"17261391101\\\",\\\"crowd_id\\\":12795,\\\"charge_rate\\\":\\\"0.0005\\\",\\\"pre_state\\\":2,\\\"after_state\\\":2,\\\"region_type\\\":1,\\\"crowd_apply_id\\\":0,\\\"recipt\\\":null,\\\"has_wallet\\\":0,\\\"wallet_moeny\\\":\\\"100.00\\\",\\\"bank_desc\\\":\\\"\\\",\\\"work_nums\\\":\\\"100.00\\\",\\\"standard\\\":\\\"1\\\",\\\"invoice_status\\\":0,\\\"enterprise_invoice_id\\\":0,\\\"work_img\\\":\\\"\\\",\\\"resolve_id\\\":2899,\\\"resolve_name\\\":\\\"1\\\",\\\"invoice_money\\\":\\\"104.99\\\",\\\"is_retry\\\":null,\\\"retry_sn\\\":null,\\\"is_ereceipt\\\":0,\\\"jc_service_rate\\\":\\\"0.0000\\\",\\\"jc_service_fee\\\":\\\"0.00\\\",\\\"jc_service_bj\\\":\\\"0.00\\\",\\\"request_no\\\":\\\"YC20230208094700001\\\"}\"}";
        FastIssuingQueryResp resp = JSONUtil.toBean(mockJo,FastIssuingQueryResp.class);
        resp.setCode("200");
        resp.setMsg("成功");
        return resp;
    }

    public static void main(String[] args) {
        YsMerConfig config = new YsMerConfig();
        String token = "sign_17a2db9f18cdd1f9741c346a739e0a19";

        FastIssuingQueryReq req = new FastIssuingQueryReq();
        req.setRequest_no("YC20230208094700007");
        FastIssuingQueryResp res = FastIssuingQueryService.exec(req,token,config);
        System.out.println(JSONUtil.toJsonStr(res));
    }
}
