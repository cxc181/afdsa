package com.yuqian.itax.yishui.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.yishui.YiShuiSupport;
import com.yuqian.itax.yishui.entity.AddEmployeeReq;
import com.yuqian.itax.yishui.entity.AddEmployeeResp;
import com.yuqian.itax.yishui.entity.YiShuiBaseResp;
import com.yuqian.itax.yishui.entity.YsMerConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 人员新增 ： http://doc.zhuoyankeji.com/web/#/13/54
 * 不支持幂等，如需调整人员信息得使用修改接口 http://doc.zhuoyankeji.com/web/#/13/56 （未调试）
 *
 *  注意：认证方式的区别
 *  1 银联四要素认证 -- 人员新增时需要提供：1）手签图片；2）自由职业合作服务协议；3）诚信纳税承诺书+税务办理授权委托书；
 *  2 电子牵四要素认证 -- 人员新增时需要提供：1）身份证正面图片，2）身份证反面图片
 *   -- 该认证方式下，需要使用到易税电子签相关接口，包括：1）申请电子签约地址；2）查询电子签约结果等；如未完成电子签，皮肤付款审核会审核不通过
 *
 * @author Administrator
 */
@Slf4j
public class AddEmployeeService extends YiShuiSupport {

    private static final String URI = "/Enterprise/addEmployee";

    public static AddEmployeeResp exec(AddEmployeeReq req, String token, YsMerConfig config){
        if(config.isMockFlag()){
            return mock(req,token,config);
        }
        AddEmployeeResp resp = new AddEmployeeResp();
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
            JSONObject dataJo = JSONUtil.parseObj(baseResp.getData());
            resp.setEnterprise_professional_facilitator_id(dataJo.getLong("enterprise_professional_facilitator_id"));
            resp.setProfessional_id(dataJo.getLong("professional_id"));
            resp.setProfessional_sn(dataJo.getStr("professional_sn"));
            return resp;
        }catch (BusinessException be){
            resp.setCode(be.getErrorCode());
            resp.setMsg(be.getMessage());
            return resp;
        }catch (Exception e){
            log.error("添加人员信息异常",e);
            resp.setCode("EX999");
            resp.setMsg("添加人员信息异常，请稍后再试");
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
    private static AddEmployeeResp mock(AddEmployeeReq req, String token,YsMerConfig config){
        AddEmployeeResp resp = new AddEmployeeResp();
        resp.setCode("200");
        resp.setMsg("成功");

        JSONObject dataOJ = new JSONObject();
        dataOJ.put("enterprise_professional_facilitator_id",3243);
        dataOJ.put("professional_id",23243);
        dataOJ.put("professional_sn","P100086");
        resp.setData(dataOJ.toStringPretty());
        return resp;
    }

    /**
     * 人员一：
     * 彭金权	6222031904000208736	430524200206262933
     * String back = "https://inabei-public.oss-cn-hangzhou.aliyuncs.com/dev/404/17261391101_idcardback1663236194877.png";
     * String front = "https://inabei-public.oss-cn-hangzhou.aliyuncs.com/dev/404/17261391101_idcardfront1663236078198.png";
     * {"msg":"人员新增成功","enterprise_professional_facilitator_id":238405,"code":"200","professional_id":284910,"professional_sn":"P0284910"}
     *
     * 人员二：
     * 程慧云 6216697500003503466 430723199211302628
     * {"enterprise_professional_facilitator_id":238406,"professional_id":284913,"professional_sn":"P0284913"}
     *
     * @param args
     */
    public static void main(String[] args) {
        String back = "https://inabei-public.oss-cn-hangzhou.aliyuncs.com/dev/404/17261391101_idcardback1663236194877.png";
        String front = "https://inabei-public.oss-cn-hangzhou.aliyuncs.com/dev/404/17261391101_idcardfront1663236078198.png";
        YsMerConfig config = new YsMerConfig();
        String token = "sign_665ca19ec229ac90391712aa03e5181e";
        AddEmployeeReq req = new AddEmployeeReq();
        req.setName("程慧云");
        req.setCer_code("430723199211302628");
        req.setBank_code("6216697500003503466");
        req.setMobile("18873685277");
        req.setSign_img(front);
        req.setProtocol_img(front);
        req.setContract_img(front);
        req.setAuth("1");
//        req.setCer_front_img(front);
//        req.setCer_reverse_img(back);
        AddEmployeeResp res = AddEmployeeService.exec(req,token,config);
        System.out.println(JSONUtil.toJsonStr(res));
    }
}
