package com.yuqian.itax.yishui.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.yishui.YiShuiSupport;
import com.yuqian.itax.yishui.entity.FastIssuingReq;
import com.yuqian.itax.yishui.entity.IssuingDataDto;
import com.yuqian.itax.yishui.entity.YiShuiBaseResp;
import com.yuqian.itax.yishui.entity.YsMerConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 第三方单号查询订单信息 ： http://doc.zhuoyankeji.com/web/#/13/63
 *
 * @author Administrator
 */
@Slf4j
public class FastIssuingService extends YiShuiSupport {

    private static final String URI = "/Enterprise/fastIssuing";

    public static YiShuiBaseResp exec(FastIssuingReq req, String token, YsMerConfig config) {
        if (config.isMockFlag()) {
            return mock(req, token, config);
        }
        YiShuiBaseResp baseResp = new YiShuiBaseResp();
        try {
            baseResp = sendToYS(req, token, URI, config);
            //失败情况下，直接返回，仅保留retCode和retMsg给业务即可
            if (!Objects.equals(baseResp.getCode(), YiShuiBaseResp.SUCCESS)) {
                return baseResp;
            }
            //调用成功，进行参数赋值
            JSONObject dataJo = JSONUtil.parseObj(baseResp.getData());
            baseResp.setData(dataJo.getStr("trade_number"));
            return baseResp;
        } catch (BusinessException be) {
            baseResp.setCode(be.getErrorCode());
            baseResp.setMsg(be.getMessage());
            return baseResp;
        } catch (Exception e) {
            log.error("佣金提现付款异常", e);
            baseResp.setCode("EX999");
            baseResp.setMsg("提现付款异常，请稍后再试");
            return baseResp;
        }
    }

    /**
     * mock返回登陆token
     *
     * @param req
     * @param config
     * @return
     */
    private static YiShuiBaseResp mock(FastIssuingReq req, String token, YsMerConfig config) {
        YiShuiBaseResp resp = new YiShuiBaseResp();
        resp.setCode("200");
        resp.setMsg("成功");
        resp.setData("YC" + System.currentTimeMillis());
        return resp;
    }

    /**
     * {"msg":"请求成功","code":"200","data":"YC20230208094700001"}
     *
     * @param args
     */
    public static void main(String[] args) {
        String str = "{\"domain\":\"http://testshuichou.zhuoyankeji.com\",\"userName\":\"国金测试\",\"password\":\"123456\",\"enterpriseSn\":\"E19577405\",\"secret\":\"c7ad028333e9b00894c23e3c4835a2e3\",\"aseKey\":\"33e9b00894c23e3c\",\"crowdId\":\"12795\",\"resolveId\":\"2899\",\"mockFlag\":false}";
        YsMerConfig config = JSONUtil.toBean(str, YsMerConfig.class);
        String token = "sign_50c76dad9f97c3c5f65d545f9fda1493";
        String orderNo = "YC202302080947000062";

        FastIssuingReq req = new FastIssuingReq();
        req.setTrade_number(orderNo);
        req.setCrowd_id(config.getCrowdId());

        IssuingDataDto dataDto = new IssuingDataDto();
        dataDto.setProfessional_id(284913L);
        dataDto.setName("程慧云");
        dataDto.setCer_code("430723199211302628");
        dataDto.setBank_code("6216697500003503466");
        dataDto.setMobile("18873685277");
        dataDto.setMoney("200.00");
        dataDto.setRemark("佣金提成");
        dataDto.setRequest_no(orderNo);
        dataDto.setResolve_id(config.getResolveId());
        /**
         * 出款银行卡ID，默认填0表示为添加人员时绑定的卡；
         * 后续如过新增了其他卡用于提现，则需要用“新增收款账户”接口返回的professional_bank_id
         */
        dataDto.setProfessional_bank_id(0L);
        req.getIssuing_data().add(dataDto);
        YiShuiBaseResp res = FastIssuingService.exec(req, token, config);
        System.out.println(JSONUtil.toJsonStr(res));
    }
}
