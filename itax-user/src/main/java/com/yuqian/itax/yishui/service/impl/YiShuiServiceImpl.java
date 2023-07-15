package com.yuqian.itax.yishui.service.impl;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.yuqian.itax.agent.entity.OemEntity;
import com.yuqian.itax.agent.entity.OemParamsEntity;
import com.yuqian.itax.agent.enums.OemParamsTypeEnum;
import com.yuqian.itax.agent.service.OemParamsService;
import com.yuqian.itax.agent.service.OemService;
import com.yuqian.itax.capital.entity.UserBankCardEntity;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.common.constants.RedisKey;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.system.service.DictionaryService;
import com.yuqian.itax.system.service.OssService;
import com.yuqian.itax.util.util.StringUtil;
import com.yuqian.itax.util.util.yishui.AESUtils;
import com.yuqian.itax.yishui.entity.*;
import com.yuqian.itax.yishui.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;


/**
 * @Description: 易税平台接口service 实现
 * @Author: lmh
 * @Date: 2023/02/20
 */
@Slf4j
@Service("yiShuiService")
public class YiShuiServiceImpl implements YiShuiService {

    @Autowired
    private OemParamsService oemParamsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OemService oemService;

    @Autowired
    private OssService ossService;

    @Autowired
    private DictionaryService dictionaryService;

    @Override
    public YsMerConfig getYsParamConfig(String oemCode, String merchantCode) throws BusinessException{
        OemParamsEntity params = null;
        if (StringUtil.isNotBlank(oemCode)) {
            params = oemParamsService.getParams(oemCode, OemParamsTypeEnum.YI_SHUI.getValue());
        } else if (StringUtil.isNotBlank(merchantCode)) {
            params = oemParamsService.getYishuiParam(merchantCode);
        }
        if (null == params) {
            throw new BusinessException("未配置易税参数");
        }

        YsMerConfig ysMerConfig = new YsMerConfig();
        ysMerConfig.setDomain(params.getUrl());
        ysMerConfig.setEnterpriseSn(params.getAccount());
        ysMerConfig.setSecret(params.getSecKey());
        ysMerConfig.setOemCode(params.getOemCode());
        JSONObject jsonObject = JSONUtil.parseObj(params.getParamsValues());
        ysMerConfig.setUserName(jsonObject.getStr("userName"));
        ysMerConfig.setPassword(jsonObject.getStr("password"));
        ysMerConfig.setAseKey(jsonObject.getStr("aseKey"));
        ysMerConfig.setCrowdId(jsonObject.getInt("crowdId"));
        ysMerConfig.setResolveId(jsonObject.getInt("resolveId"));
        return ysMerConfig;
    }

    /**
     * 发送请求到易税灵工
     * @param busiObj  - 业务接口参数对象
     * @param token  - 登陆token
     * @param config   - 商户配置
     * @return
     * @throws BusinessException
     */
    public static YiShuiBaseResp sendToYS(Object busiObj, String token, String uri, YsMerConfigVO config) throws BusinessException{
        //1.处理业务数据处理
        JSONObject requestJo = new JSONObject();
        requestJo.put("token",token);
        requestJo.put("data",busiObj);
        log.info("业务请求数据:" + JSONUtil.toJsonStr(requestJo));
        String response = null;
        try{
            String url = config.getDomain() + uri;
            HttpResponse httpResponse = HttpRequest.post(url).body(JSONUtil.toJsonStr(requestJo))
                    .timeout(10000).contentType(ContentType.JSON.toString()).execute();
            int httpStatus = httpResponse.getStatus();
            if(httpStatus != HttpStatus.SC_OK){
                log.error("业务请求处理失败,HttpStatus:"+httpStatus);
                throw new BusinessException("业务请求失败,httpStatus:"+httpStatus);
            }
            response = httpResponse.body();
            log.info("业务请求返回数据:"+response);
            if(!JSONUtil.isJson(response)){
                log.error("业务请求处理失败,返回数据不是JSON格式");
                throw new BusinessException("业务请求失败,上游返回数据格式异常");
            }
        }catch (BusinessException be){
            throw new BusinessException(Integer.valueOf(YiShuiBaseResp.UNKNOWN_ERR),be.getLocalizedMessage());
        }catch (Exception e){
            log.error("业务请求处理失败,请求上游接口异常",e);
            throw new BusinessException(Integer.valueOf(YiShuiBaseResp.UNKNOWN_ERR),"上游接口异常，请稍后再试");
        }
        JSONObject resJO = JSONUtil.parseObj(response);
        Integer code = resJO.getInt("code");
        String msg = resJO.getStr("msg");
        YiShuiBaseResp baseResp = new YiShuiBaseResp();
        baseResp.setCode(code+"");
        baseResp.setMsg(msg);
        if(Objects.equals("200",baseResp.getCode())){
            //数据解密
            try{
                String data = resJO.getStr("data");
                if(!JSONUtil.isJson(data)){
                    data = AESUtils.decrypt(data,config.getAseKey());
                }
                baseResp.setData(data);
            } catch (Exception e){
                throw new BusinessException(Integer.valueOf(YiShuiBaseResp.UNKNOWN_ERR),"上游接口异常，请稍后再试");
            }
        }
        return baseResp;
    }


    @Override
    public String getToken(String oemCode) throws BusinessException {
        String redisTokenKey = RedisKey.YISHUI_TOKEN_SUFFER + oemCode;
        //如果redis存在token，返回token
        String token = redisService.get(redisTokenKey);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        YsMerConfig config = this.getYsParamConfig(oemCode, null);
        EnterpriseLoginReq req = new EnterpriseLoginReq();
        req.setUser_name(config.getUserName());
        req.setPassword(config.getPassword());
        req.setTimestamp(System.currentTimeMillis() + "");
        token = EnterpriseLoginService.exec(req, config);
        redisService.set(redisTokenKey, token, 9 * 60);
        return token;
    }

    @Override
    public YiShuiBaseResp pay(String oemCode,  String orderNo, BigDecimal money, UserBankCardEntity userBankCardEntity, Long professionalId) throws BusinessException {
        //获取配置信息
        YsMerConfig config = this.getYsParamConfig(oemCode, null);
        FastIssuingReq fastIssuingReq = new FastIssuingReq();
        fastIssuingReq.setTrade_number(orderNo);
        fastIssuingReq.setCrowd_id(config.getCrowdId());
        IssuingDataDto issuingDataDto = new IssuingDataDto();
        issuingDataDto.setProfessional_id(professionalId);
        issuingDataDto.setName(userBankCardEntity.getUserName());
        issuingDataDto.setCer_code(userBankCardEntity.getIdCard());
        issuingDataDto.setMobile(userBankCardEntity.getPhone());
        issuingDataDto.setBank_code(userBankCardEntity.getBankNumber());

        issuingDataDto.setMoney(money.toPlainString());
        issuingDataDto.setRemark("推广服务费结算");
        issuingDataDto.setRequest_no(orderNo);
        issuingDataDto.setProfessional_bank_id(userBankCardEntity.getProfessionalBankId());
        issuingDataDto.setResolve_id(config.getResolveId());
        fastIssuingReq.setIssuing_data(Lists.newArrayList(issuingDataDto));
        //调用易税接口
        return FastIssuingService.exec(fastIssuingReq, getToken(oemCode), config);
    }

    @Override
    public FastIssuingQueryResp payResultQuery(String oemCode, String orderNo) throws BusinessException {
        //获取配置信息
        YsMerConfig config = this.getYsParamConfig(oemCode, null);
        FastIssuingQueryReq fastIssuingQueryReq = new FastIssuingQueryReq();
        fastIssuingQueryReq.setRequest_no(orderNo);
        return FastIssuingQueryService.exec(fastIssuingQueryReq, getToken(oemCode), config);
    }

    @Override
    public void changeOrderStatus(String oemCode, String enterpriseOrderId) throws BusinessException {
        //获取配置信息
        YsMerConfig config = this.getYsParamConfig(oemCode, null);
        ChangeOrderStatusReq req = new ChangeOrderStatusReq();
        req.setEnterprise_order_id(enterpriseOrderId);
        req.setStatus(1);
        req.setRemarks("审核通过");
        // 查询机构信息
        OemEntity oem = oemService.getOem(oemCode);
        if (null == oem) {
            throw new BusinessException("未查询到机构信息");
        }
        String head = dictionaryService.getValueByCode("oss_req_head");
        String publicBucketName = dictionaryService.getValueByCode("oss_publicBucketName");
        String endpoint = dictionaryService.getValueByCode("oss_endpoint");
        String sealImg = head + publicBucketName + "." + endpoint + "/" + oem.getOfficialSealImgPublic();
        req.setSeal_img(sealImg);
        ChangeOrderStatusService.exec(req, getToken(oemCode), config);
    }


}
